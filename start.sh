#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}Starting Alerte Project Servers...${NC}"

# Function to handle cleanup on exit
cleanup() {
    echo -e "\n${YELLOW}Stopping servers...${NC}"
    kill $(jobs -p) 2>/dev/null
    exit
}

trap cleanup SIGINT SIGTERM EXIT

# Function to process and colorize output
process_logs() {
    local prefix=$1
    local default_color=$2
    while IFS= read -r line; do
        # 1. Errors in RED
        if [[ $line =~ ERROR|Error|Exception|failed|Refused|FAILURE ]]; then
            echo -e "${RED}$prefix $line${NC}"
        
        # 2. Success/Started in GREEN
        elif [[ $line =~ "Started AlerteServerApplication" ]]; then
            echo -e "${GREEN}$prefix ✅ SERVER STARTED SUCCESSFULLY!${NC}"
            echo -e "${BLUE}$prefix ➜ Backend URL: http://localhost:8080${NC}"
            echo -e "${default_color}$prefix $line${NC}"
        
        elif [[ $line =~ "Application bundle generation complete" ]]; then
            echo -e "${GREEN}$prefix ✅ FRONTEND READY!${NC}"
            echo -e "${default_color}$prefix $line${NC}"

        # 3. Addresses in BLUE
        elif [[ $line =~ "Local:" || $line =~ "Network:" ]]; then
            echo -e "${BLUE}$prefix $line${NC}"
        
        # Default output
        else
            echo -e "${default_color}$prefix $line${NC}"
        fi
    done
}

# Start Backend
if [ -d "AlerteServer" ]; then
    echo -e "${CYAN}Launching Backend...${NC}"
    export JAVA_HOME=/home/antoine/jdk-21
    export PATH=$JAVA_HOME/bin:$PATH
    (cd AlerteServer && ./mvnw spring-boot:run -Dmaven.test.skip=true) 2>&1 | process_logs "[Backend]" "${NC}" &
else
    echo -e "${RED}Error: AlerteServer directory not found${NC}"
fi

# Start Frontend
if [ -d "AlerteWeb" ]; then
    echo -e "${CYAN}Launching Frontend...${NC}"
    (cd AlerteWeb && npm start) 2>&1 | process_logs "[Frontend]" "${NC}" &
else
    echo -e "${RED}Error: AlerteWeb directory not found${NC}"
fi

# Wait for all background processes
wait
