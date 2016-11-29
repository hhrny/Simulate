find Simulate/src -name \*.java > javafile
if [ -d Simulate/bin ]
then
    javac -cp ".:/lib/*" -d Simulate/bin @javafile
else
    mkdir Simulate/bin
    javac -cp ".:/lib/*" -d Simulate/bin @javafile
fi
rm javafile
