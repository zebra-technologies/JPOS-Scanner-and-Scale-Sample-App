package JposTest.src;

import jpos.JposException;


@FunctionalInterface 
interface CmdDirectIO<command, inxml, opcode>{
    void Set(String[] c, String[] i, int[] o) throws JposException;
}
/**
 * This class is used to bind the InXml and the OperationalCode with their
 * respective DirectIO Command. Use getInXml() and getOpCode() methods to get
 * the inXml and opCode
 *
 * @author CS7291
 * @version 1.0
 *
 */
public class DirectIOBinding {

    private final String inXml;
    private final String command;
    private final int opCode;

    /**
     * Constructor creates instances to each DirectIOCommand with its inXml and
 opCode.
     * @param command : directIO command to be performed
     * @param inXml :InXml of the relevant command
     * @param opCode : opCode of the command
     */
    public DirectIOBinding( String command,String inXml, int opCode) {
        this.inXml = inXml;
        this.command = command;
        this.opCode = opCode;
    }

    
    /**
     * Returns the InXml of the relevant DirectIO Command which is a String
     * value.
     *
     * @return InXml
     */
    public String getInXml() {
        return inXml;
    }

    /**
     * Returns the Operation code of the relevant DirectIO Command which is an
     * Integer value.
     *
     * @return OPeration Code
     */
    public int getOpCode() {
        return opCode;
    }

    
    @Override
    public String toString() {
        return command;
    }
}
