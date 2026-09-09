#!/bin/bash
# TEST
# TODO: FINISH THIS FIRST RECONSTRUCT THE CODE

clean() {
    find . -name "*.class" -type f -delete
    echo "Cleaned all .class files"
}

function compile(){
    java_compile="javac -d class -cp .:drivers/jbcrypt-0.4.jar:source/util/drivers/sqlite-jdbc-3.53.2.0.jar source/model/*.java source/service/*.java source/util/*.java source/Main.java"
    printf "$java_compile\n\n"
    $java_compile
}

function execute(){
    java_execute="java -cp class:source/util/drivers/jbcrypt-0.4.jar:source/util/drivers/sqlite-jdbc-3.53.2.0.jar source.Main"
    printf "$java_execute\n\n"

    echo "Executing..."

    $java_execute
}

clean
compile
status=$?
echo status: $status

if [ $status -eq 0 ]; then 
    echo "STATUS: Compiled Success"
    execute
    status=$?

    if [ $status -eq 0 ]; then 
        echo "STATUS: Execute Success"
    else  
        echo "STATUS: Execute Failed"
    fi

else  
    echo "STATUS: Compiled Failed"
fi



