# ExternalSorting
Yet another external sorting project
## Implementation details
Simple 2-way merging approach has been used:
1. First original file is split to two temporary files, containing sorted sequences of strings
	- We read as many strings as can be fitted to memory (one by one and using another file as a buffer to not to overflow memory)
	- Then this chunk is sorted using MinHeap and dumped to one of the two output files and current output file is switched to other one
2. Then this two files are processed and two new files are obtained as a result, which contain smaller number of sorted sequences of strings
	- We read sorted string sequences from the beginning of the input files and write a merged sorted sequence to current output file from the two output files
	- We change output file to other one
	- Repeat steps above while have string to read
3. Step 2 is repeated until there is only one sorted sequence.
