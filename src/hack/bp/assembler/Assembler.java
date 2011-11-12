package hack.bp.assembler;

import java.io.*;
import java.util.Hashtable;

/** Assembler.java **********************************************************************
 * 	This is the implementation of the Hack assembler.
 * 
 * 	@author bp
 * 
 * 	@changes
 * 	0.1 -	Initial implementation (cannot handle C_COMMANDs, L_COMMANDs). Single-pass,
 * 			will translate A_COMMANDs. -bp
 * 	0.2	-	Single pass. Can handle C_COMMANDs. -bp
 * 	0.3 - 	Completed min requirements. The only thing you cannot do is use in-line
 * 			comments. However, if the comments are situated in it's own line, you can
 * 			write a full program that uses variables. The RAM address is not checked
 * 			for overflow. -bp
 *
 ***************************************************************************************/
public class Assembler 
{
	private final static int DEF_SYM_TABLE_CNT = 8;
	private static int varStartAddress = 16;
	private static Hashtable<String, Integer> m_symbolTable = null;

	/** main() **************************************************************************
	 *  Fires off the assembler with run(). This function makes sure that an argument 
	 *  has been passed-in before running the assembler.
	 ***********************************************************************************/
	public static void main( String[] args ) 
	{
		// Check if the file is passed in
		if( args.length > 0 )
		{
			if( args[ 0 ].endsWith( ".asm" ) )
				run( args[ 0 ] );
			else
				System.out.println( "Usage: <program> <fileName> " +
						"\n\t -Can only use file with .asm extension.");
		}
		else
			System.out.println( "Usage: <program> <fileName> " +
					"\n\t -Enter an .asm file.");
	}	

	/** init() **************************************************************************
	 *  Pre-defined symbols for assembler. 
	 ***********************************************************************************/
	private static void init()
	{
		// Create the symbol table;
		if( m_symbolTable == null )
			m_symbolTable = new Hashtable<String, Integer>( DEF_SYM_TABLE_CNT );

		// Add all the pre-defined symbols before run() is launched!
		m_symbolTable.put( "SP", 	 	0 );
		m_symbolTable.put( "LCL",  	1 );
		m_symbolTable.put( "ARG",  	2 );
		m_symbolTable.put( "THIS", 	3 );
		m_symbolTable.put( "THAT", 	4 );
		m_symbolTable.put( "R0",	 	0 );
		m_symbolTable.put( "R1",	 	1 );
		m_symbolTable.put( "R2",	 	2 );
		m_symbolTable.put( "R3",	 	3 );
		m_symbolTable.put( "R4",	 	4 );
		m_symbolTable.put( "R5",	 	5 );
		m_symbolTable.put( "R6",	 	6 );
		m_symbolTable.put( "R7",	 	7 );
		m_symbolTable.put( "R8",	 	8 );
		m_symbolTable.put( "R9",	 	9 );
		m_symbolTable.put( "R10",	 	10 );
		m_symbolTable.put( "R11",	 	11 );
		m_symbolTable.put( "R12",	 	12 );
		m_symbolTable.put( "R13",	 	13 );
		m_symbolTable.put( "R14",	 	14 );
		m_symbolTable.put( "R15",	   	15 );
		m_symbolTable.put( "SCREEN", 	16384 );
		m_symbolTable.put( "KBD", 	24576 );
		
		System.out.println( "Init: Completed pre-populating symbols table.." );
	}

	/** getNextAvailableAddress() *******************************************************
	 *  Will return the next available slot in the RAM that a variable can occupy.
	 *  This does not check for overflow.
	 ***********************************************************************************/
	public static int getNextAvailableAddress()
	{
		return varStartAddress;
	}
	
	/** setNextAvailableAddress() *******************************************************
	 *  Sets the next available address.
	 ***********************************************************************************/
	public static void setNextAvailableAddress( int nextAddress )
	{
		varStartAddress = nextAddress;
	}
	
	/** getAddress() *******************************************************
	 *  Returns the address associated with the symbol.
	 ***********************************************************************************/
	public static int getAddress( String symbol )
	{
		return m_symbolTable.get( symbol );
	}

	/** run() ***************************************************************************
	 *  This is the implementation of the assembler. It contains a timer accurate to
	 *  the nano-second to measure the assembler's performance.
	 ***********************************************************************************/
	public static void run( String fileName )
	{
		System.out.println( "Starting assembler..." );
		// Initialize the symbol table
		init();
		
		// Start the timer for run()
		long timerStart = System.nanoTime();

		// Run the assembler passes
		firstPass( fileName );
		secondPass( fileName );

		// Print the compilation statistics on screen (timer and success msg)
		long timerEnd = System.nanoTime();
		System.out.println( "Assembly completed! (elapsed time: " +
				( timerEnd - timerStart ) + "ns)\n");
	}

	/** firstPass() *********************************************************************
	 *  This builds the symbol table. It is the first pass in the assembly process.
	 ************************************************************************************/
	private static void firstPass( String fileName )
	{
		// Start timer
		long timerStart = System.nanoTime();
		
		// Report status
		System.out.println( "Starting first pass: Populating symbol table..." );
		
		// Open a new parser
		Parser parser = new Parser( fileName );
		
		// Initialize the current line
		parser.setCurrentLineNumber( 0 );

		// Run through each line
		while( parser.hasMoreCommands() )
		{
			// Get the command
			parser.advance();

			// A_COMMAND/C_COMMAND - Update next address
			if( ( parser.commandType() == Parser.Commands.A_COMMAND ) ||
					( parser.commandType() == Parser.Commands.C_COMMAND ) )
				parser.setCurrentLineNumber( parser.getCurrentLineNumber() + 1 );
				
			// L_COMMAND - Populate the symbol table with the labels. Do not update address.
			if( parser.commandType() == Parser.Commands.L_COMMAND )
			{
				// Check if the symbol table has the symbol, if not, store in table
				if( !m_symbolTable.containsKey( parser.symbol() ) )
					m_symbolTable.put( parser.symbol(), parser.getCurrentLineNumber() );
				else
				{
					System.out.println( "Assembly file contains multiple symbols of the same label!" );
					System.exit( 1 );
				}
			}
			
			// BAD_COMMAND - Do nothing for comments. Do not update the address.
			if( parser.commandType() == Parser.Commands.BAD_COMMAND )
			{}
		}
		
		// Report results of first pass
		long timerEnd = System.nanoTime();
		System.out.println( "First pass completed! (elapsed time: " +
				( timerEnd - timerStart ) + "ns)");
	}

	/** secondPass() ********************************************************************
	 *  This builds the entire output using information from the symbol table.
	 ************************************************************************************/
	private static void secondPass( String fileName )
	{
		// Start timer
		long timerStart = System.nanoTime();
		
		// Report status
		System.out.println( "Starting second pass: Creating binaries..." );
		
		// Initialize the parser and default output to write to file
		Parser parser = new Parser( fileName );
		String output = "";

		// Initialize the address size (defined in Hack machine code spec)
		final int ADDRESS_LENGTH = 15;

		// Initialize the line number
		parser.setCurrentLineNumber( 0 );
		
		// Start reading the file
		while( parser.hasMoreCommands() )
		{
			// Grab the next command and store it as the current command
			parser.advance();

			// Handle BAD_COMMAND - Comments
			if( parser.commandType() == Parser.Commands.BAD_COMMAND )
				parser.setCurrentLineNumber( parser.getCurrentLineNumber() + 1 );
			
			// Handle L_COMMAND - Labels
			if( parser.commandType() == Parser.Commands.L_COMMAND )
				parser.setCurrentLineNumber( parser.getCurrentLineNumber() + 1 );
		
			// Handle A_COMMAND - Address instructions
			if( parser.commandType() == Parser.Commands.A_COMMAND )
			{
				char symbolArray[] = parser.symbol().toCharArray();
				boolean symbolHasLetter = false;

				// Check if the symbol has a letter
				for( int i = 0; i < symbolArray.length; i++ )
				{
					symbolHasLetter = Character.isLetter( symbolArray[ i ] );
					
					// Break from loop if letter found
					if( symbolHasLetter )
						break;
				}

				// Update the address to load next
				parser.setCurrentLineNumber( parser.getCurrentLineNumber() + 1 );
				
				// Initialize the variable to hold the calculated binary string
				String tempCode = "";
				int decAddress = -1;

				// Calculate and store the binary string (if label, look up table)
				if( !symbolHasLetter )
					decAddress = Integer.parseInt( parser.symbol() );
				else
				{
					// Look up if the variable is in the table
					if( m_symbolTable.containsKey( parser.symbol() ) )
						decAddress = getAddress( parser.symbol() );
					else
					{
						// Add the variable to symbol table under RAM address (16 and on)
						m_symbolTable.put( parser.symbol(), getNextAvailableAddress() );
						
						// Update the available address
						setNextAvailableAddress( getNextAvailableAddress() + 1 );
						
						// Set the new variable's address
						decAddress = getAddress( parser.symbol() );
					}
				}
				
				// Generate the initial binary string
				tempCode = Integer.toBinaryString( decAddress );

				// Format the binary string to meet machine code specs
				if( ( tempCode.length() < ADDRESS_LENGTH ) && ( tempCode.length() > 0 ) )
				{
					int paddingCount = ADDRESS_LENGTH - tempCode.length();

					// Append the A_COMMAND prefix
					output += "0";

					// Pad the output to conform to the machine code specs
					for( int i = 0; i < paddingCount; i++ )
						output += "0";

					// Create the machine code and start new line for next code
					output += tempCode;
					output += "\n";
				}
				
			}	

			// Handle C_COMMAND
			if( parser.commandType() == Parser.Commands.C_COMMAND )
			{
				// Initialize the mnemonic translator
				Code code = new Code();

				// Update the address of next command to load
				parser.setCurrentLineNumber( parser.getCurrentLineNumber() + 1 );
			
				// Append the C_COMMAND prefix
				output += "111";

				// Construct the machine code
				output += code.comp( parser.comp() ) + 
						code.dest( parser.dest() ) + 
						code.jump( parser.jump() );
				output += "\n";	
			}
		}
		
		// Write to file (<filename-minus-extension>.hack)
		try
		{
			// Create buffered writer
			FileWriter fileStream = new FileWriter( fileName.replace( ".asm", ".hack" ) );
			BufferedWriter out = new BufferedWriter( fileStream );

			// Write to file and then close the writer
			out.write( output, 0, output.length() );
			out.close();  
		}
		catch (Exception e)
		{
			System.err.println( "Error: " + e.getMessage() );
			e.printStackTrace();
			System.exit( 1 );
		}
		
		// Report results of second pass
		long timerEnd = System.nanoTime();
		System.out.println( "Second pass completed! (elapsed time: " +
				( timerEnd - timerStart ) + "ns)");
	}

}
