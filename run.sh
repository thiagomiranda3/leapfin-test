numThreads=10
timeout=60000
logLevel=INFO

function help() {
    echo "Using: $0 [-n thread number] [-t program timeout] [-l log level]"
    echo "  -n   Thread number, default=10"
    echo "  -t,  Timeout in milli for all threads, default=60000"
    echo "  -l,  Log level for the application, default=INFO, options : [ERROR, WARN, INFO, DEBUG]"
    echo "Example: $0 -n 10 -t 500 -l INFO"

    exit 1
}

while getopts n:t:l:h flag; do
    case "${flag}" in
        n) numThreads=${OPTARG};;
        t) timeout=${OPTARG};;
        l) logLevel=${OPTARG};;
        h) help ;;
        *) help ;;
    esac
done

java -DnumThreads=${numThreads} -Dtimeout=${timeout} -DlogLevel=${logLevel} -jar target/leapfin-test-1.0-SNAPSHOT.jar