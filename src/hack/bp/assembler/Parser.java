package hack.bp.assembler;

import java.io.*;
import java.util.Scanner;

/****************************************************************************************
 *  Encapsulates access to the input code. Reads an assembly language command, parses it,
 *  and provides convenient access to the command's components (fields and symbols). In
 *  addition, removes all whitespace and comments.
 *  
 *  This version uses JDK 1.6 update 29. Please have this when attempting to run this
 *  program.
 *  
 *  @author bp
 *  
 *	@changes
 *	0.1 - 	Initial implementation of Parser class. This 
 *			version will not translate programs that contain 
 *			symbols. -bp
 *	0.2 -	Fixing access specifiers for a few methods. 
 *			Adding more useful comments -bp
 *	0.3 -	Tons of fixes. Can now parse symbols. -bp
 *	0.4	-	More useful functions. -bp
 *	0.5	-	Minor fixes. -bp
 *	0.6 - 	Added ability to parse comments. -bp
 *	0.7 -	Minor fixes. - bp
 ***************************************************************************************/
public class Parser 
{
	private File m_inputFile = null;
	private Scanner m_scanner = null;
	private String m_currentCommand = "";
	private int m_commandLength = -1;
	private int m_currentLineNumber = -1;

	public Parser()
	{
		exit();
	}
	
	public Parser( String filePath )
	{
		init( filePath );
	}
	
	/************************************************************************************
	 *  Initializes the Parser instance with the current file and then sets up the
	 *  reader. A scanner is initialized to allow class methods to read the file's
	 *  contents. *** THIS MUST ALWAYS BE CALLED FIRST BEFORE USING THIS PARSER! ***
	 *  Not doing so will keep the input file object and scanner null (therefore
	 *  breaking the functionality of methods that depend on them). This is generally
	 *  used only in the constructor.
	 ***********************************************************************************/
	private void init( String filePath )
	{
		// Initialize the file
		if( this.m_inputFile == null )
			this.m_inputFile = new File( filePath );
		
		// Create a scanner to easily check file contents
		try 
		{
			this.m_scanner = new Scanner( m_inputFile );
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println( "ParserError-init: Scanner couldn't find file!" );
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	
	/************************************************************************************
	 * 	Re-initializes the object properly after a new inputFile has been introduced
	 *  to the stream.
	 ***********************************************************************************/
	private void reinit( File inputFile )
	{
		// Close the current scanner so everything is fresh
		this.m_scanner.close();
		
		this.m_inputFile = inputFile;
		
		// Reset the default values
		resetCurrentCommand();
		resetCommandLength();
		resetCurrentLineNumber();
		
		// Create a scanner to easily check file contents
		try 
		{
			this.m_scanner = new Scanner( m_inputFile );
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println( "ParserError-reinit: Scanner couldn't find file!" );
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	
	/************************************************************************************
	 *  It is common to not use System.exit() from within a class constructor. Instead
	 *  it is better to let a method handle that function.
	 ***********************************************************************************/
	private void exit()
	{
		System.out.println( "ParserError-exit(): Parser must be initialized " +
							"with a filePath!" );
		System.exit( 1 );
	}
	
	/************************************************************************************
	 *  Enumerations for the commands-types that exist for HACK (minus BAD_COMMAND).
	 *  BAD_COMMAND is used strictly to set a default value for the command type in-case
	 *  an unrecognized command is given. This, along with the L_COMMAND does not
	 *  exist in the actual Hack Architecture specification.
	 ***********************************************************************************/
	public static enum Commands { A_COMMAND, C_COMMAND, L_COMMAND, BAD_COMMAND }
	
	/************************************************************************************
	 *  Returns the current inputFile assigned to the Parser instance.  
	 ***********************************************************************************/
	public File getInputFile() 
	{
		return m_inputFile;
	}
	
	/************************************************************************************
	 *  Sets the current inputFile assigned to the Parser instance. Once this is used,
	 *  the instance must be reinitialized to notice the new file! 
	 ***********************************************************************************/
	private void setInputFile( File inputFile ) 
	{
		reinit( inputFile );
	}

	/************************************************************************************
	 *  Returns the current command. 
	 ***********************************************************************************/
	public String getCurrentCommand() 
	{
		return m_currentCommand;
	}
	
	/** *********************************************************************************
	 *  Returns the current command without any white spaces. 
	 ***********************************************************************************/
	public String getCurrentCommandWithoutWhiteSpaces()
	{
		return getCurrentCommand().replaceAll( "\\s", "" );
	}
	
	/************************************************************************************
	 *  Sets the current command.
	 ***********************************************************************************/
	private void setCurrentCommand( String command ) 
	{
		this.m_currentCommand = command;
	}
	
	/************************************************************************************
	 *  Resets the current command back to default state.
	 ***********************************************************************************/
	private void resetCurrentCommand()
	{
		this.m_currentCommand = "";
	}
	
	/************************************************************************************
	 *  Returns the current command's length.
	 ***********************************************************************************/
	public int getCommandLength() 
	{
		return m_commandLength;
	}
	
	/************************************************************************************
	 *  Returns the current command's length without spaces.
	 ***********************************************************************************/
	public int getCommandLengthWithoutWhiteSpaces()
	{
		return getCurrentCommandWithoutWhiteSpaces().length();
	}
	
	/************************************************************************************
	 *  Sets the current command's length.
	 ***********************************************************************************/
	private void setCommandLength ( int commandLength )
	{
		this.m_commandLength = commandLength;
	}
	
	/************************************************************************************
	 *  Resets the current command length back to default state.
	 ***********************************************************************************/
	private void resetCommandLength()
	{
		this.m_commandLength = -1;
	}
	
	/************************************************************************************
	 *  Grabs the current line number.
	 ***********************************************************************************/
	public int getCurrentLineNumber() 
	{
		return m_currentLineNumber;
	}

	/************************************************************************************
	 *  Set the current command's line number. (This is used to determine addresses
	 *  on memory). When a block label has been encountered, this is used to determimine
	 *  the address of the first line of the block by adding 1 to the current line's
	 *  number.
	 ***********************************************************************************/
	public void setCurrentLineNumber(int currentLineNumber) 
	{
		this.m_currentLineNumber = currentLineNumber;
	}
	
	/************************************************************************************
	 *  Resets the current line number back to the default state.
	 ***********************************************************************************/
	private void resetCurrentLineNumber()
	{
		this.m_currentLineNumber = -1;
	}
	
	/************************************************************************************
	 *  Checks if there are more commands in the input. Stores a command
	 ***********************************************************************************/
	public boolean hasMoreCommands()
	{
		boolean hasMoreCommands = false;
		
		// Check if there is input in the next line, set boolean flag true if so
		if( this.m_scanner.hasNextLine() )
			hasMoreCommands = true;
		
		return hasMoreCommands;
	}
	
	/************************************************************************************
	 *  Reads the next command from the input and makes it the current command. Should
	 *  be called only if hasMoreCommands() is true. Initially there is no current
	 *  command.
	 ***********************************************************************************/
	public void advance()
	{
		// Read in a line and set it as the current command
		setCurrentCommand( this.m_scanner.nextLine() );
		
		// Read the current command and set the command length
		setCommandLength( getCurrentCommand().length() );
	}
	
	/************************************************************************************
	 *  Returns the type of the current command:
	 *  	- A_COMMAND for @Xxx where Xxx is either a symbol or a decimal number.
	 *  	- C_COMMAND for dest=comp;jump
	 *  	- L_COMMAND a pseudo-command for (Xxx) where Xxx is a symbol.
	 *  	- BAD_COMMAND is used to handle an unrecognized command.
	 ***********************************************************************************/
	public Commands commandType()
	{
		// Initialize default command
		Commands cmd = Commands.BAD_COMMAND;
		
		// A-register commands always contain @ as a prefix (@(xxx) is allowed)
		if( getCurrentCommand().contains( "@" ) )
			cmd = Commands.A_COMMAND;
		
		// Computational command in form of comp;jump
		if( ( getCurrentCommand().contains( ";" )  ||
				getCurrentCommand().contains( "=" ) ) &&
				( !getCurrentCommand().contains( "@" ) ) )
			cmd = Commands.C_COMMAND;
		
		// Handles (Xxx) labels
		if( getCurrentCommand().contains( "(" ) &&
			getCurrentCommand().contains( ")" ) &&
			( !getCurrentCommand().contains( "@" ) ) )
		{
			cmd = Commands.L_COMMAND;
		}		
		
		// Handles comments (this does not handle in-line comments)
		if( getCurrentCommand().contains( "//" ) )
			cmd = Commands.BAD_COMMAND;
		
		return cmd;
	}
	
	/************************************************************************************
	 *  Returns the symbol or decimal Xxx of the current command @Xxx or (Xxx). Should
	 *  be called only when commandType() is A_COMMAND or L_COMMAND.
	 ***********************************************************************************/
	public String symbol()
	{
		// Initialize symbol and start indices
		String symbol = "";
		int startIndexAmp = -1;
		int startIndexLeftParen = -1;
		int endIndexRightParen = -1;
		
		// Grab the trimmed command starting after the @
		if( ( getCommandLengthWithoutWhiteSpaces() > 0 ) && 
			( getCurrentCommandWithoutWhiteSpaces().contains( "@" ) ) )
		{
			startIndexAmp = getCurrentCommandWithoutWhiteSpaces().indexOf( "@" );
			symbol = getCurrentCommandWithoutWhiteSpaces().substring( startIndexAmp + 1 );
		}
		
		// Handle (Xxx) case where Xxx is a label
		if( ( getCommandLengthWithoutWhiteSpaces() > 0 ) && 
			( !getCurrentCommandWithoutWhiteSpaces().contains( "@" ) ) &&
			( getCurrentCommandWithoutWhiteSpaces().contains( "(" ) ) &&
			( getCurrentCommandWithoutWhiteSpaces().contains( ")" ) ) )
		{
			startIndexLeftParen = getCurrentCommandWithoutWhiteSpaces().indexOf( "(" );
			endIndexRightParen	= getCurrentCommandWithoutWhiteSpaces().indexOf( ")" );
			
			symbol = getCurrentCommandWithoutWhiteSpaces()
					.substring( startIndexLeftParen + 1, endIndexRightParen );
		}
		
		// Do nothing if a comment is encountered
		if( ( getCommandLengthWithoutWhiteSpaces() > 0 ) &&
				( getCurrentCommandWithoutWhiteSpaces().contains( "//" ) ) )
			symbol = "";
		
		return symbol;
	}
	
	/************************************************************************************
	 *  Returns the dest mnemonic in the current C-command (8 possibilities). Should
	 *  be called only when commandType() is C_COMMAND.
	 ***********************************************************************************/
	public String dest()
	{
		// Initialize dest
		String dest = "";
		
		if( ( getCommandLengthWithoutWhiteSpaces() > 0 ) &&
				getCurrentCommandWithoutWhiteSpaces().contains( "=" ) )
		{
			// Split up the dest=comp command with the equal sign
			String[] commandPieces = getCurrentCommandWithoutWhiteSpaces().split( "=" );
			
			// Take the first piece (the dest portion)
			dest = commandPieces[ 0 ];
		}
		
		// Do nothing if a comment is encountered
		if( ( getCommandLengthWithoutWhiteSpaces() > 0 ) &&
				( getCurrentCommandWithoutWhiteSpaces().contains( "//" ) ) )
			dest = "";
	
		return dest;
	}
	
	/************************************************************************************
	 *  Returns the comp mnemonic in the current C-command (28 possibilities). Should be
	 *  called only when commandType() is C_COMMAND;
	 ***********************************************************************************/
	public String comp()
	{
		// Initialize comp
		String comp = "";
		
		// Prepare the string array to hold command pieces after split
		String[] commandPieces = null;
		
		// dest=comp case
		if( ( getCommandLengthWithoutWhiteSpaces() > 0 ) && 
				getCurrentCommandWithoutWhiteSpaces().contains( "=" ) )
		{
			// Split up the dest=comp command with the equal sign
			commandPieces = getCurrentCommandWithoutWhiteSpaces().split( "=" );
			
			// Take the second piece (comp portion) 
			comp = commandPieces[ 1 ];
		}
		
		// comp;jump case
		if( ( getCommandLengthWithoutWhiteSpaces() > 0 ) && 
				getCurrentCommandWithoutWhiteSpaces().contains( ";" ) )
		{
			// Split up the comp;jump command with the semi-colon
			commandPieces = getCurrentCommandWithoutWhiteSpaces().split( ";" );
			
			// Take the first piece (comp portion) 
			comp = commandPieces[ 0 ];
		}
		
		// Do nothing if a comment is encountered
		if( ( getCommandLengthWithoutWhiteSpaces() > 0 ) &&
				( getCurrentCommandWithoutWhiteSpaces().contains( "//" ) ) )
			comp = "";
					
		return comp;
	}
	
	/************************************************************************************
	 *  Returns the jump mnemonic in the current C-command (8 possibilities). Should be
	 *  called only when the commandType() is C_COMMAND;
	 ***********************************************************************************/
	public String jump()
	{
		// Initialize jump
		String jump = "";
				
		// comp;jump case
		if( ( getCommandLengthWithoutWhiteSpaces() > 0 ) && 
				getCurrentCommandWithoutWhiteSpaces().contains( ";") )
		{
			// Split up the comp;jump command with the semi-colon
			String[] commandPieces = getCurrentCommandWithoutWhiteSpaces().split( ";" );
					
			// Take the second piece (jump portion)
			jump = commandPieces[ 1 ];
		}
		
		// Do nothing if a comment is encountered
		if( ( getCommandLengthWithoutWhiteSpaces() > 0 ) &&
				( getCurrentCommandWithoutWhiteSpaces().contains( "//" ) ) )
			jump = "";
		
		return jump;
	}
}