#
# Run perf tests. Usage:
#
# perf.sh TYPE SIZE ITER [ADDR]
#
#   TYPE: inproc_lat, inproc_thr, pair_lat, pair_thr
#   SIZE: message size in bytes
#   ITER: number of iterations
#   ADDR: address used for PAIR tests
#

type="$1"
size="$2"
iter="$3"
addr="$4"

pack="org.nanomsg"
prog1=""
prog2=""

if [ "$type" == "inproc_lat" ]
then
  args="${size} ${iter}"
  prog1="inproc_lat"
elif [ "$type" == "inproc_thr" ]
then
  args="${size} ${iter}"
  prog1="inproc_thr"
elif [ "$type" == "pair_lat" ]
then
  args="${addr} ${size} ${iter}"
  prog1="local_lat"
  prog2="remote_lat"
elif [ "$type" == "pair_thr" ]
then
  args="${addr} ${size} ${iter}"
  prog1="local_thr"
  prog2="remote_thr"
fi

if [ "$prog1" == "" -a "$prog2" == "" ]
then
  echo "Don't know how to do that" 1>&2
  exit 0
fi

out1="/tmp/perf_$$_${prog1}.out"
done1="/tmp/perf_$$_${prog1}.done"
out2="/tmp/perf_$$_${prog2}.out"
done2="/tmp/perf_$$_${prog2}.done"

trap "/bin/rm -f $out1 $done1 $out2 $done2" EXIT

function run_prog() {
  prog="$1"
  args="$2"
  outn="$3"
  donn="$4"

  rm -f "$outn" "$donn"
  if [ "$prog" == "" ]
  then
    touch "$donn"
  else
    echo "Running $prog $args"
    class="$pack.$prog"
    (ant -Dcn="$class" -Dargs="$args" perf; touch "$donn") >"$outn" 2>&1 &
  fi
}

function results() {
  out="$1"

  cat "$out" |
    egrep '^[ 	]+\[java\]' |
    sed -e 's/^[ 	]*\[java\] //g' -e 's/\([0-9]\),\([0-9]\)/\1.\2/g' |
    egrep -v '^(NANO|args:) '
}

run_prog "$prog1" "$args" "$out1" "$done1"
run_prog "$prog2" "$args" "$out2" "$done2"

echo -n "Waiting for programs to finish..."
while true
do
  c=`ls -1 /tmp/*.done 2>&1 | egrep "^($done1|$done2)$" | wc -l`
  if [ $c -eq 2 ]
  then
    break
  fi
  sleep 1
done
echo " Done"
echo "------"

if [ "$type" == "inproc_lat" ]
then
  results "$out1"
elif [ "$type" == "inproc_thr" ]
then
  results "$out1"
elif [ "$type" == "pair_lat" ]
then
  results "$out2"
elif [ "$type" == "pair_thr" ]
then
  results "$out1"
fi
echo "======"
