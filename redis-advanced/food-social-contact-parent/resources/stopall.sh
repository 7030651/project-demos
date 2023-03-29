command="kill -9"
for num in `jps | grep jar | awk '{print $1}'` 
do
    command+=" ${num}"
done;
echo "ready to exec command: $command"
eval $command
echo "ready to exec jps"
eval jps
