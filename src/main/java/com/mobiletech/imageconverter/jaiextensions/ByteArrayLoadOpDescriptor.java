package com.mobiletech.imageconverter.jaiextensions;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OperationRegistry;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.registry.RenderedRegistryMode;

import com.sun.media.jai.codec.ImageDecodeParam;

public class ByteArrayLoadOpDescriptor extends OperationDescriptorImpl {
	private static boolean registered = false;
    /**
     * The resource strings that provide the general documentation and
     * specify the parameter list for the "FileLoad" operation.
     */
    private static final String[][] resources = {
        {"GlobalName",  "byteArrayLoad"},
        {"LocalName",   "byteArrayLoad"},
        {"Vendor",      "com.sun.media.jai"},
        {"Description", "FileLoadDescriptor0"},
        {"DocURL",      "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/FileLoadDescriptor.html"},
        {"Version",     "DescriptorVersion"},
        {"arg0Desc",    "FileLoadDescriptor1"},
        {"arg1Desc",    "FileLoadDescriptor4"},
	{"arg2Desc",    "FileLoadDescriptor5"}
    };

    /** The parameter names for the "FileLoad" operation. */
    private static final String[] paramNames = {
        "bytearrayinputstream", "param", "checkFileLocally"
    };

    /** The parameter class types for the "FileLoad" operation. */
    private static final Class[] paramClasses = {
    	com.sun.media.jai.codec.ByteArraySeekableStream.class,
        com.sun.media.jai.codec.ImageDecodeParam.class,
        java.lang.Boolean.class
    };

    /** The parameter default values for the "FileLoad" operation. */
    private static final Object[] paramDefaults = {
        NO_PARAMETER_DEFAULT, null, Boolean.TRUE
    };

    /** Constructor. */
    public ByteArrayLoadOpDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    /**
     * Validates the input parameters.
     *
     * <p> In addition to the standard checks performed by the
     * superclass method, this method by default checks that the source file
     * exists and is readable. This check may be bypassed by setting the
     * <code>checkFileLocally</code> parameter to <code>FALSE</code>
     */
    protected boolean validateParameters(ParameterBlock args,
                                         StringBuffer msg) {
        return true;
    }


    /**
     * Reads an image from a file.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all
     * supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     *
     * @param filename The path of the file to read from.
     * @param param The ImageDecodeParam to use.
     * May be <code>null</code>.
     * @param checkFileLocally Boolean specifying if File existence should be checked locally
     * May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use.
     * May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>filename</code> is <code>null</code>.
     */
    public static RenderedOp create(ByteArrayInputStream bytearray,
                                    ImageDecodeParam param,
                                    Boolean checkFileLocally,
                                    RenderingHints hints)  {
        ParameterBlockJAI pb =
            new ParameterBlockJAI("byteArrayLoad",
                                  RenderedRegistryMode.MODE_NAME);

        pb.setParameter("bytearrayinputstream", bytearray);
        pb.setParameter("param", param);
        pb.setParameter("checkFileLocally", checkFileLocally);

        return JAI.create("byteArrayLoad", pb, hints);
    }
    
    /**
	  * A method to register this operator with the OperationRegistry and
	  * RIFRegistry. 
	  */
	  public synchronized static void register(){
		  if (!registered){
			  // Get the OperationRegistry.
			  OperationRegistry op = JAI.getDefaultInstance().getOperationRegistry();
			if(op.getDescriptor("rendered","byteArrayLoad") == null){		      
			// Register the operator's descriptor. 
			ByteArrayLoadOpDescriptor desc = new ByteArrayLoadOpDescriptor();	      
			op.registerDescriptor(desc);
			// Register the operators's RIF.
			ByteArrayLoadOpRIF rif = new ByteArrayLoadOpRIF();
			RIFRegistry.register(op,"byteArrayLoad","br.inpe.lac",rif);
			registered = true;
			}
	  }
	}
	  
	  
}
