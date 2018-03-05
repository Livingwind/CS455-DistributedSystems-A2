  for i in `cat machine_list`
  do
    COMMAND='ping -c 1'
    OPTION=$i
    COMMAND+=" $OPTION"
    eval $COMMAND &
  done
