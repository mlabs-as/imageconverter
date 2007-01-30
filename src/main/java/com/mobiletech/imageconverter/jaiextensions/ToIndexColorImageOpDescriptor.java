package com.mobiletech.imageconverter.jaiextensions;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OperationRegistry;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ROI;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ColorQuantizerType;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.registry.RenderedRegistryMode;
import javax.media.jai.util.Range;

public class ToIndexColorImageOpDescriptor extends OperationDescriptorImpl {
    /** The predefined color quantization algorithms. */
    /** The pre-defined median-cut color quantization algorithm. */
    public static final ToIndexColorImageType MEDIANCUT =
        new ToIndexColorImageType("MEDIANCUT", 1);
    /** The pre-defined NeuQuant color quantization algorithm. */
    public static final ToIndexColorImageType NEUQUANT =
        new ToIndexColorImageType("NEUQUANT", 2);
    /** The pre-defined Oct-Tree color quantization algorithm. */
    public static final ToIndexColorImageType OCTTREE =
        new ToIndexColorImageType("OCTTREE", 3);

    private static boolean registered = false;
    /**
     * The resource strings that provide the general documentation
     * and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName",  "toIndexColorImage"},
        {"LocalName",   "toIndexColorImage"},
        {"Vendor",      "com.sun.media.jai"},
        {"Description", "ColorQuantizerDescriptor0"},
        {"DocURL",      "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ColorQuantizerDescriptor.html"},
        {"Version",     "DescriptorVersion2"},
        {"arg0Desc",    "ColorQuantizerDescriptor1"},
        {"arg1Desc",    "ColorQuantizerDescriptor2"},
        {"arg2Desc",    "ColorQuantizerDescriptor3"},
        {"arg3Desc",    "ColorQuantizerDescriptor4"},
        {"arg4Desc",    "ColorQuantizerDescriptor5"},
        {"arg5Desc",    "ColorQuantizerDescriptor6"},
        {"arg6Desc",    "ColorQuantizerDescriptor7"},
        {"arg6Desc",    "ColorQuantizerDescriptor7"},
        {"arg6Desc",    "ColorQuantizerDescriptor7"},
    };

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {
        "quantizationAlgorithm",
        "maxColorNum",
        "upperBound",
        "roi",
        "xPeriod",
        "yPeriod",
        "transColor",
        "colorMap",
        "colorModel"
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {
    	com.mobiletech.imageconverter.jaiextensions.ToIndexColorImageType.class,
        java.lang.Integer.class,
        java.lang.Integer.class,
        javax.media.jai.ROI.class,
        java.lang.Integer.class,
        java.lang.Integer.class,
        java.awt.Color.class,
        javax.media.jai.LookupTableJAI.class,
        java.awt.image.ColorModel.class
    };

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {
        MEDIANCUT,
        new Integer(256),
        null,
        null,
        new Integer(1),
        new Integer(1),
        null,
        null,
        null
    };

    private static final String[] supportedModes = {
        "rendered"
    };

    /** Constructor. */
    public ToIndexColorImageOpDescriptor() {
        super(resources, supportedModes, 1,
              paramNames, paramClasses, paramDefaults, null);

    }

    /**
     * Returns the minimum legal value of a specified numeric parameter
     * for this operation.
     */
    public Range getParamValueRange(int index) {
        switch (index) {
        case 1:
        case 2:
        case 4:
        case 5:        
            return new Range(Integer.class, new Integer(1), null);
        }        
        return null;
    }

    /**
     * Returns <code>true</code> if this operation is capable of handling
     * the input parameters.
     *
     * <p> In addition to the default validations done in the super class,
     * this method verifies that the provided quantization algorithm is one of
     * the three predefined algorithms in this class.
     *
     * @throws IllegalArgumentException  If <code>args</code> is <code>null</code>.
     * @throws IllegalArgumentException  If <code>msg</code> is <code>null</code>
     *         and the validation fails.
     */
    protected boolean validateParameters(String modeName,
                                         ParameterBlock args,
                                         StringBuffer msg) {
        if ( args == null || msg == null ) {
            throw new IllegalArgumentException("Generic0");
        }

        if (!super.validateParameters(modeName, args, msg))
            return false;

        ToIndexColorImageType algorithm =
            (ToIndexColorImageType)args.getObjectParameter(0);
        if (algorithm != MEDIANCUT && algorithm != NEUQUANT &&
            algorithm != OCTTREE) {
            msg.append(getName() + " " +
                       "ColorQuantizerDescriptor7");
            return false;
        }

        Integer secondOne = (Integer)args.getObjectParameter(2);
        if (secondOne == null) {
            int upperBound = 0;
            if (algorithm.equals(MEDIANCUT))
                upperBound = 32768;
            else if (algorithm.equals(NEUQUANT))   // set the cycle for train to 100
                upperBound = 100;
            else if (algorithm.equals(OCTTREE))    // set the maximum tree size to 65536
                upperBound = 65536;

            args.set(upperBound, 2);
        }

        return true;
    }

    /**
     * Color quantization on the provided image.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all
     * supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     *
     * @param source0 <code>RenderedImage</code> source 0.
     * @param algorithm The algorithm to be chosen.  May be <code>null</code>.
     * @param maxColorNum The maximum color number.  May be <code>null</code>.
     * @param upperBound An algorithm-dependent parameter.  See the parameter
     *                   table above.  May be <code>null</code>.
     * @param roi The region of interest.  May be <code>null</code>.
     * @param xPeriod The X subsample rate.  May be <code>null</code>.
     * @param yPeriod The Y subsample rate.  May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use.
     * May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0,
                                    ColorQuantizerType algorithm,
                                    Integer maxColorNum,
                                    Integer upperBound,
                                    ROI roi,
                                    Integer xPeriod,
                                    Integer yPeriod,
                                    Color col,
                                    LookupTableJAI colorMap,
                                    ColorModel cm,
                                    RenderingHints hints)  {
        ParameterBlockJAI pb =
            new ParameterBlockJAI("toIndexColorImage",
                                  RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("quantizationAlgorithm", algorithm);
        pb.setParameter("maxColorNum", maxColorNum);
        pb.setParameter("upperBound", upperBound);
        pb.setParameter("roi", roi);
        pb.setParameter("xPeriod", xPeriod);
        pb.setParameter("yPeriod", yPeriod);
        pb.setParameter("transColor", col);
        pb.setParameter("colorMap",colorMap);
        pb.setParameter("colorModel", cm);

        return JAI.create("toIndexColorImage", pb, hints);
    }
	  
	 /**
	  * A method to register this operator with the OperationRegistry and
	  * RIFRegistry. 
	  */
	  public synchronized static void register()
	    {
	    if (!registered)
	      {
//	    	 Get the OperationRegistry.
		      OperationRegistry op = JAI.getDefaultInstance().getOperationRegistry();
	    	if(op.getDescriptor("rendered","toIndexColorImage") == null){		      
		      // Register the operator's descriptor. 
		      ToIndexColorImageOpDescriptor desc = new ToIndexColorImageOpDescriptor();	      
		      op.registerDescriptor(desc);
		      // Register the operators's RIF.
		      ToIndexColorImageOpRIF rif = new ToIndexColorImageOpRIF();
		      RIFRegistry.register(op,"toIndexColorImage","br.inpe.lac",rif);
		      registered = true;
	    	}
	      }
	    }
	  
	  }
