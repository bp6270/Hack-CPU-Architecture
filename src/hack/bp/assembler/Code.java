package hack.bp.assembler;

/****************************************************************************************
 * 	This file is used to translate Hack Architecture mnemonics to its equivalent binary 
 *  form.	
 * 
 * 	@author bp
 *	
 *	@changes
 *	0.1 - 	Initial implementation. Note that switch( string ) 
 *			is not used since that is only part of the JDK 7
 *			SDK. Since JDK 1.6 is still common and widely-used,
 *			only the if/if-else will be used, despite being 
 *			super messy. It is still faster than implementing
 *			using a hash-table. -bp
 *
 *	0.2 -	Making significant fixes to the equality check.
 *			Ignoring case completely to fix issue reading
 *			string passed to it from the parser. This has
 *			no side-effect. It actually allows flexibility
 *			by not requiring all characters to be in caps. -bp
 *
 ***************************************************************************************/

public class Code 
{
	/************************************************************************************
	 *  Returns the machine code that corresponds to the dest mnemonics. (8 types)
	 ***********************************************************************************/
	public String dest( String mnemonic )
	{
		String stringBits = "";
		
		if( mnemonic.equalsIgnoreCase( "" ) )
		{
			stringBits = "000";
		}
		
		else if( mnemonic.equalsIgnoreCase( "M" ) )
		{
			stringBits = "001";
		}
		
		else if( mnemonic .equalsIgnoreCase( "D" ) )
		{
			stringBits = "010";
		}
		
		else if( mnemonic.equalsIgnoreCase( "MD" ) )
		{
			stringBits = "011";
		}
		
		else if( mnemonic.equalsIgnoreCase( "A" ) )
		{
			stringBits = "100";
		}
		
		else if( mnemonic.equalsIgnoreCase( "AM" ) )
		{
			stringBits = "101";
		}
		
		else if( mnemonic.equalsIgnoreCase( "AD" ) )
		{
			stringBits = "110";
		}
		
		else if( mnemonic.equalsIgnoreCase( "AMD" ) )
		{
			stringBits = "111";
		}
	
		return stringBits;	
	}
	
	/************************************************************************************
	 *  Returns the machine code that corresponds to the comp mnemonics. Note
	 *  that the 'a' bit in a C_COMMAND is appended as a prefix in the 
	 *  stringBits. (28 types)
	 ***********************************************************************************/
	public String comp( String mnemonic )
	{
		String stringBits = "";
		
		if( mnemonic.equalsIgnoreCase( "0" ) )
		{
			stringBits = "0101010";
		}
		
		else if( mnemonic.equalsIgnoreCase( "1" ) )
		{
			stringBits = "0111111";
		}
		
		else if( mnemonic.equalsIgnoreCase( "-1" ) )
		{
			stringBits = "0111010";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D" ) )
		{
			stringBits = "0001100";
		}
		
		else if( mnemonic.equalsIgnoreCase( "A" ) )
		{
			stringBits = "0110000";
		}
		
		else if( mnemonic.equalsIgnoreCase( "M" ) )
		{
			stringBits = "1110000";
		}
		
		else if( mnemonic.equalsIgnoreCase( "!D" ) )
		{
			stringBits = "0001101";
		}
		
		else if( mnemonic.equalsIgnoreCase( "!A" ) )
		{
			stringBits = "0110011";
		}
		
		else if( mnemonic.equalsIgnoreCase( "!M" ) )
		{
			stringBits = "1110001";
		}
		
		else if( mnemonic.equalsIgnoreCase( "-D" ) )
		{
			stringBits = "0001111";
		}
		
		else if( mnemonic.equalsIgnoreCase( "-A" ) )
		{
			stringBits = "0110011";
		}
		
		else if( mnemonic.equalsIgnoreCase( "-M" ) )
		{
			stringBits = "1110011";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D+1" ) )
		{
			stringBits = "0011111";
		}
		
		else if( mnemonic.equalsIgnoreCase( "A+1" ) )
		{
			stringBits = "0110111";
		}
		
		else if( mnemonic.equalsIgnoreCase( "M+1" ) )
		{
			stringBits = "1110111";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D-1" ) )
		{
			stringBits = "0001110";
		}
		
		else if( mnemonic.equalsIgnoreCase( "A-1" ) )
		{
			stringBits = "0110010";
		}
		
		else if( mnemonic.equalsIgnoreCase( "M-1" ) )
		{
			stringBits = "1110010";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D+A" ) )
		{
			stringBits = "0000010";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D+M" ) )
		{
			stringBits = "1000010";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D-A" ) )
		{
			stringBits = "0010011";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D-M" ) )
		{
			stringBits = "1010011";
		}
		
		else if( mnemonic.equalsIgnoreCase( "A-D" ) )
		{
			stringBits = "0000111";
		}
		
		else if( mnemonic.equalsIgnoreCase( "M-D" ) )
		{
			stringBits = "1000111";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D&A" ) )
		{
			stringBits = "0000000";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D&M" ) )
		{
			stringBits = "1000000";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D|A" ) )
		{
			stringBits = "0010101";
		}
		
		else if( mnemonic.equalsIgnoreCase( "D|M" ) )
		{
			stringBits = "1010101";
		}
		
		return stringBits;
	}
	
	/************************************************************************************
	 *  Returns the machine code that corresponds to the jump mnemonics.
	 ***********************************************************************************/
	public String jump( String mnemonic )
	{
		String stringBits = "";
		
		if( mnemonic.isEmpty() )
		{
			stringBits = "000";
		}
		
		else if( mnemonic.equalsIgnoreCase( "JGT" )  )
		{
			stringBits = "001";
		}
		
		else if( mnemonic.equalsIgnoreCase( "JEQ" ) )
		{
			stringBits = "010";
		}
		
		else if( mnemonic.equalsIgnoreCase( "JGE" ) )
		{
			stringBits = "001";
		}
		
		else if( mnemonic.equalsIgnoreCase( "JLT" ) )
		{
			stringBits = "100";
		}
		
		else if( mnemonic.equalsIgnoreCase( "JNE" ) )
		{
			stringBits = "101";
		}
		
		else if( mnemonic.equalsIgnoreCase( "JLE" ) )
		{
			stringBits = "110";
		}
		
		else if( mnemonic.equalsIgnoreCase( "JMP" ) )
		{
			stringBits = "111";
		}
		
		return stringBits;
	}
}
