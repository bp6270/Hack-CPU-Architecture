// Test
		@i
		M=1
		@sum
		M=0
// First block
(LOOP)	
		@i
		D=M
		@100
		D=D-A
		@END
		D;JGT
		@i
		D=M
		@sum
		M=D+M
		@i
		M=M+1
		@LOOP
		0;JMP
// Second block
(END)	
		@END
		0;JMP
