for i in `cat machine_list`
do
  ssh $i "killall -u $USER java"
done