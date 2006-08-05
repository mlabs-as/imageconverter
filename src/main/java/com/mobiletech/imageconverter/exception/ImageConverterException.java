/*
 * Created on Mar 28, 2005
 *
 */
package com.mobiletech.imageconverter.exception;

/**
 * @author Andreas Ryland
 */
public class ImageConverterException extends Exception {

	private static final long serialVersionUID = 777;

	private int errorCode = 0;

	private ImageConverterException() {
	}

    public String toString(){       
        return getErrorMessageHeader() + super.toString();
    }
    
    public String getMessage(){
        return getErrorMessageHeader() + super.getMessage();
    }
    
    private String getErrorMessageHeader(){
        switch(errorCode){
            case Types.BAD_INPUT_VARIABLE:
                return "ImageConverterException [ BAD_INPUT_VARIABLE ] -> ";
            case Types.BAD_RESIZE_SIZE:
                return "ImageConverterException [ BAD_RESIZE_SIZE ] -> ";
            case Types.CODEC_NOT_FOUND:
                return "ImageConverterException [ CODEC_NOT_FOUND ] -> ";
            case Types.CODEC_NOT_SUPPORTED:
                return "ImageConverterException [ CODEC_NOT_SUPPORTED ] -> ";
            case Types.COLOR_REDUCER_ERROR:
                return "ImageConverterException [ COLOR_REDUCER_ERROR ] -> ";
            case Types.EMBEDDED_EXCEPTION:
                return "ImageConverterException [ EMBEDDED_EXCEPTION ] -> ";
            case Types.IO_ERROR:
                return "ImageConverterException [ IO_ERROR ] -> ";
            case Types.READ_CODEC_NOT_FOUND:
                return "ImageConverterException [ READ_CODEC_NOT_FOUND ] -> ";
        }
        return "ImageConverterException [ UNDEFINED ] -> ";     
    }
    
	public ImageConverterException(int inErrorCode, String inMessage,
			Throwable inThrowable) {
		super(inMessage, inThrowable);

		errorCode = inErrorCode;
	}

	/**
	 * @return Returns the errorCode.
	 */
	public int getErrorCode() {
		return errorCode;
	}

	public class Types {
		/**
		 * used if a conversion to an unsupported format is requested
		 */
		public final static int CODEC_NOT_SUPPORTED = 1;

		/**
		 * used if the image codec to be used cannot be found
		 */
		public final static int CODEC_NOT_FOUND = 2;

		/**
		 * used if resize arguments are 0 or negative
		 */
		public final static int BAD_RESIZE_SIZE = 3;

		/**
		 * used if the JINI ColorReducer class produces an error
		 */
		public final static int COLOR_REDUCER_ERROR = 4;

		/**
		 * used if the convertTo string is empty or null or the byte[] does not
		 * contain any data
		 */
		public final static int BAD_INPUT_VARIABLE = 5;

		/**
		 * used if an IOException is thrown
		 */
		public final static int IO_ERROR = 6;

		/**
		 * used if the image passed to the imageconverter has a format that
		 * doesnt have an available reader
		 */
		public final static int READ_CODEC_NOT_FOUND = 7;
        
        /**
         * used if the image passed to the imageconverter has a format that
         * doesnt have an available reader
         */
        public final static int EMBEDDED_EXCEPTION = 8;
	}
}