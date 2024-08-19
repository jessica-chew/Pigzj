Pigzj is a Java implementation of pigz. This reads from stdin, compresses it, and writes into stdout. Pigzj takes
optional arguments '-p n' where n is the specified number of threads to use.

If the arguments to Pigzj are incorrect, the program will exit with nonzero status. An few examples of this is if we were to run 
java Pigzj -n 4
java Pigzj -n4
java Pigzj -p "random_string"


My implementation of Pigzj takes breaks the input data into blocks of 128 KB each, assigns the blocks to available threads, and merges the compressed
data after compressing in parallel. It uses a round robin scheduler to ensure efficiency in assigning tasks to runnable or waiting threads.
To ensure that the compressed data is merged in the correct order (which we have to consider with parallel programming), I used a ConcurrentHashMap
to map the block number to the specific data block that is being compressed. My mergeCompressedBlocks function then merges the blocks accordingly.


Below are the CPU times to compress the following file using gzip, pigz, and Pigzj,
where input=/usr/local/cs/jdk-21.0.2/lib/modules

Using default settings (number of threads used is available number of threads)
GZIP:
bash-4.4$ time gzip <$input >gzip.gz

real    0m8.463s
user    0m8.307s
sys     0m0.073s

bash-4.4$ time gzip <$input >gzip.gz

real    0m8.598s
user    0m8.330s
sys     0m0.123s

bash-4.4$ time gzip <$input >gzip.gz

real    0m8.529s
user    0m8.271s
sys     0m0.108s


PIGZ:
bash-4.4$ time pigz <$input >pigz.gz

real    0m4.419s
user    0m8.290s
sys     0m0.144s
bash-4.4$ time pigz <$input >pigz.gz

real    0m3.988s
user    0m8.417s
sys     0m0.126s
bash-4.4$ time pigz <$input >pigz.gz

real    0m2.897s
user    0m8.249s
sys     0m0.155s


PIGZJ:
bash-4.4$ time java Pigzj <$input >Pigzj.gz

real    0m3.909s
user    0m7.919s
sys     0m0.231s

bash-4.4$ time java Pigzj <$input >Pigzj.gz

real    0m3.707s
user    0m8.157s
sys     0m0.235s


bash-4.4$ time java Pigzj <$input >Pigzj.gz

real    0m3.514s
user    0m7.821s
sys     0m0.238s


Now using a specified number of threads as our argument: -p 30

PIGZ:
bash-4.4$ time pigz -p 30 <$input >pigz.gz

real    0m2.524s
user    0m8.307s
sys     0m0.185s
bash-4.4$ time pigz -p 30 <$input >pigz.gz

real    0m2.393s
user    0m8.314s
sys     0m0.187s
bash-4.4$ time pigz -p 30 <$input >pigz.gz

real    0m2.569s
user    0m8.480s
sys     0m0.172s

PIGZJ:
bash-4.4$ time java Pigzj -p 30 <$input >Pigzj.gz

real    0m2.795s
user    0m7.858s
sys     0m0.226s
bash-4.4$ time java Pigzj -p 30 <$input >Pigzj.gz

real    0m2.905s
user    0m7.810s
sys     0m0.244s
bash-4.4$ time java Pigzj -p 30 <$input >Pigzj.gz

real    0m2.921s
user    0m7.845s
sys     0m0.239s

Testing with '-p 1'
PIGZ:
bash-4.4$ time pigz -p 1 <$input >pigz.gz

real    0m8.299s
user    0m7.932s
sys     0m0.152s
bash-4.4$ time pigz -p 1 <$input >pigz.gz

real    0m8.359s
user    0m8.040s
sys     0m0.085s
bash-4.4$ time pigz -p 1 <$input >pigz.gz

real    0m8.386s
user    0m7.946s
sys     0m0.105s

PIGZJ:
time java Pigzj -p 1 <$input >Pigzj.gz

real    0m8.471s
user    0m7.722s
sys     0m0.274s
bash-4.4$ time java Pigzj -p 1 <$input >Pigzj.gz

real    0m8.322s
user    0m7.752s
sys     0m0.212s
bash-4.4$ time java Pigzj -p 1 <$input >Pigzj.gz

real    0m8.482s
user    0m7.863s
sys     0m0.252s

original file size: 139257677 bytes
Pigzj.gz size: 47967407 bytes
pigz.gz size: 47008845 bytes
Compression ratio (using Pigzj.gz): roughly 34.4%
Compression ratio (using pigz.gz): roughly 33.7%

NOW USING A VERY LARGE FILE ON GZIP, PIGZ, PIGZJ, DEFAULT OPTIONS!
GZIP:
bash-4.4$ time gzip <input >gzip.gz

real    0m4.278s
user    0m4.072s
sys     0m0.155s
bash-4.4$ time gzip <input >gzip.gz

real    0m4.120s
user    0m3.949s
sys     0m0.138s
bash-4.4$ time gzip <input >gzip.gz

real    0m4.329s
user    0m3.949s
sys     0m0.142s

PIGZ:
bash-4.4$ time pigz <input >pigz.gz

real    0m10.306s
user    0m4.786s
sys     0m0.904s

bash-4.4$ time pigz <input >pigz.gz

real    0m3.094s
user    0m4.940s
sys     0m0.275s
bash-4.4$ time pigz <input >pigz.gz

real    0m2.707s
user    0m5.017s
sys     0m0.291s

PIGZJ:
bash-4.4$ time java Pigzj <input >Pigzj.gz

real    0m1.633s
user    0m4.013s
sys     0m0.569s
bash-4.4$ time java Pigzj <input >Pigzj.gz

real    0m1.847s
user    0m4.196s
sys     0m0.472s
bash-4.4$ time java Pigzj <input >Pigzj.gz

real    0m1.790s
user    0m4.024s
sys     0m0.544s

ORIGINAL FILE SIZE: 536870912 bytes
Compressed size (Pigzj.gz): 659456 bytes
Compressed size (pigz.gz): 585745 bytes
Compression ratio (Pigzj): approx. 0.1%
Compression ratio (pigz): approx. 0.1%

Running strace:
bash-4.4$ strace -o gzip_trace.txt gzip < $input > output_file.gz
bash-4.4$ strace -o pigz_trace.txt pigz < $input > output_file.gz
bash-4.4$ strace -o pigzj_trace.txt java Pigzj < $input > output_file.gz

Looking at the output from strace, gzip_trace.txt contains 4252 read calls, pigz has 1076, and pigzj_trace has 20.
In addition, we see that there are 5 clone calls in pigz_trace and 1 clone call in pigzj_trace. 

These results indicate that gzip is less efficient than the other two compression programs, given that there is calling
overhead with gzip having 4x the calls that pigz has. The cloning in pigz and Pigzj take advantage of parallel compression,
also contributing to its efficiency over gzip, evident in the metrics we have above.

Pigzj and pigz likely handle large files more efficiently than gzip, given the multi-threaded approach to compression.
Increasing the number of threads may improve efficiency, but we must also account of the overhead in thread management in the
multi-threaded approaches, which may outweigh the increase in performance. In general, pigz would probably work the best with large files,
while gzip would suffice when compressing smaller files since it is single threaded and avoids the overheard of multi threading. We can see that
when we test with '-p 1', the performance of Pigzj and pigz is approximately the same as gzip.