numThreads=10
timeout=60000
logLevel=INFO

function help() {
    if [[ -n "$1" ]]; then
        echo "$1";
    fi

    echo "Help: $0 [-n thread number] [-t program timeout] [-l log level]"
    echo "  -n   Thread number, default=10"
    echo "  -t,  Timeout in milli for all threads, default=60000"
    echo "  -l,  Log level for the application, default=INFO, options : [ERROR, WARNING, INFO, DEBUG]"
    echo ""
    echo "Example: $0 -n 2 -t 10 -l INFO"

    exit 1
}

while getopts n:t:l:h: flag
do
    case "${flag}" in
        n) numThreads=${OPTARG};;
        t) timeout=${OPTARG};;
        l) logLevel=${OPTARG};;
        h) usage ;;
        *) usage "Unknown parameter passed: ${OPTARG}";;
    esac
done

if [[ -z "$numThreads" ]]; then
    usage "Number of workers is not set";
fi

if [[ -z "$timeout" ]]; then
    usage "Timeout is not set.";
fi

if [[ -z "$logLevel" ]]; then
    usage "Log level is not set.";
fi

java -DnumThreads=${numThreads} -Dtimeout=${timeout} -DlogLevel=${logLevel} -jar target/leapfin-test-1.0-SNAPSHOT.jar