IFS=$'\n' read -d '' -r -a lines < jar-list

for line in "${lines[@]}"
do
  # echo "line = $line"
  directory=$(echo $line | awk '{print $1}')
  jar=$(echo $line | awk '{print $2}')
  log=$(echo $line | awk '{print $3}')
  command="nohup java -jar $jar > $log &"
  # echo $command
  cd $directory
  eval $command
  cd ..
done
