CLASSES=${PWD}
SCRIPT="cd $CLASSES;
java cs455.scaling.client.Client 129.82.44.157 33000 5"
#$1 is the command-line argument
for ((j=1;j<=$1;j++));
do
  COMMAND='gnome-terminal'
  for i in `cat machine_list`
  do
    echo 'logging into '$i
    OPTION='--tab -e "ssh -t '$i' '$SCRIPT'"'
    COMMAND+=" $OPTION"
  done
  eval $COMMAND &
done
