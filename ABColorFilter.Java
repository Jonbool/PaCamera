

public class ABColorFilter extends ShaderProgram {

	private static final String mABColorShader =
            "precision mediump float;\n"
            +"uniform sampler2D tex_sampler_0;\n"
            +"uniform sampler2D tex_sampler_1;\n"
            + "varying vec2 v_texcoord;\n"
			+ "float contrast = 1.08;\n"//[0,4]
            +" const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n"
            + " vec4 rgba;\n"
            
            + " void main() {\n"
          	+ " vec4 origin = texture2D(tex_sampler_0, v_texcoord);\n"
          	+ " vec4 blur = texture2D(tex_sampler_1, v_texcoord);\n"
 			+ "vec4 textureColor = 0.80*origin + 0.20*blur;\n"
//			White Beauty
// 			+ "float beta = 5.0;\n"//[2,6]
// 			+ "textureColor.rgb = log(textureColor.rgb*(beta - 1.0) + 1.0) / log(beta);\n"
//			Contrast Adjust	
		    + " rgba = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);\n" 

//          obtain the Saturation
            + "float saturation = 1.3;\n"//0.6
            + "lowp float luminance = dot(rgba.rgb, luminanceWeighting);\n" 
            + "lowp vec3 greyScaleColor = vec3(luminance);\n" 
            + "\n"
            + "rgba = vec4(mix(greyScaleColor, rgba.rgb, saturation), rgba.w);\n"
            
//          adjusting brightness
            + " rgba = 0.85*rgba;\n"   
//          Set threshold		
            + " float T = 0.008856;\n"
            + " float T1 = 0.206893;\n"   
//          define variable
            + " float X,Y,Z,fY,fX,fZ,L,a,b ;\n"      
            
//        	RGB to Lab 
//          RGB to XYZ
            + " X = rgba.r*0.412453 + rgba.g*0.357580 + rgba.b*0.180423;\n "
            + " Y = rgba.r*0.212671 + rgba.g*0.715160 + rgba.b*0.072169;\n "
            + " Z = rgba.r*0.019334 + rgba.g*0.119193 + rgba.b*0.950227;\n "
//       	Normalize for D65 white point
 			+ " X = X/0.950456;\n"
 			+ " Y = Y;\n"
 			+ " Z = Z/1.088754;\n"
// 			
 			+ " if (Y>T)\n"
 			+ " {\n"
		    + "	  fY = pow(Y,1.0/3.0);\n"
 			+ "   L = 116.0*fY - 16.0;\n"
 			+ " }\n"
 			+ " else\n"
 			+ " {\n"
 			+ "   fY = 7.787*Y + 16.0/116.0;\n"
 			+ "   L = 903.3*Y;\n"
 			+ " }\n"			
// 			
 			+ " if (X>T)\n"
 			+ " {\n"
		    + "	  fX = pow(X,1.0/3.0);\n"
 			+ " }\n"
 			+ " else\n"
 			+ " {\n"
 			+ "   fX = 7.787*X + 16.0/116.0;\n"
 			+ " }\n"		
// 			
 			+ " if (Z>T)\n"
 			+ " {\n"
		    + "	  fZ = pow(Z,1.0/3.0);\n"
 			+ " }\n"
 			+ " else\n"
 			+ " {\n"
 			+ "   fZ = 7.787*Z + 16.0/116.0;\n"
 			+ " }\n"	
// 			
			+ "	a = 500.0*(fX - fY);\n"
			+ "	b = 200.0*(fY - fZ);\n"

//			Processing
			+ "	L = 1.08*L;\n"//1.05
			+ "	b = 1.3*a;\n"//1.3
			+ "	a = 1.0*a;\n"
			
//		   	Lab to RGB
//			Compute Y
			+ "	fY = (L + 16.0)/116.0;\n"
			+ "	fY = fY*fY*fY;\n"		
			
 			+ " if (fY>T)\n"
 			+ " {\n"
		    + "	  Y = fY;\n"
 			+ " }\n"
 			+ " else\n"
 			+ " {\n"
 			+ "   fY = L/903.3;\n"
 			+ " }\n"
// 			Alter fY slightly for further calculations
 			+ " if (fY>T)\n"
 			+ " {\n"
		    + "	  fY = pow(fY,1.0/3.0);\n"
 			+ " }\n"
 			+ " else\n"
 			+ " {\n"
 			+ "   fY = 7.787*fY + 16.0/116.0;\n"
 			+ " }\n"	
// 			Compute X
			+ "	fX = a/500.0 + fY;\n"
 			+ " if (fX>T1)\n"
 			+ " {\n"
		    + "	  X = pow(fX,3.0);\n"
 			+ " }\n"
 			+ " else\n"
 			+ " {\n"
 			+ "   X = (fX - 16.0/116.0)/7.787;\n"
 			+ " }\n"		
//			Compute Z
			+ "	fZ =  fY - b/200.0;\n"	
 			+ " if (fZ>T1)\n"
 			+ " {\n"
		    + "	  Z = pow(fZ,3.0);\n"
 			+ " }\n"
 			+ " else\n"
 			+ " {\n"
 			+ "   Z = (fZ - 16.0/116.0)/7.787;\n"
 			+ " }\n"
// 			Normalize for D65 white point
			+ "	X =  X*0.950456;\n"  
			+ "	Z =  Z*1.088754;\n"  		
//			XYZ to RGB	    
            + " float R = 3.240479*X - 1.537150*Y - 0.498535*Z;\n "
            + " float G = -0.969256*X + 1.875992*Y + 0.041556*Z;\n "
            + " float B = 0.055648*X - 0.204043*Y + 1.057311*Z;\n "
//		    Processing
			+ "	rgba.r = 0.95*R + 0.05*rgba.r;\n"  	
//			+ "	rgba.g = 1.2*G - 0.2*rgba.g;\n"  
//			+ "	rgba.b = 1.2*B - 0.2*rgba.b;\n"  //+ 0.2*rgba.g;
			+ "	rgba.g = 0.85*G + 0.2*rgba.g;\n"  
			+ "	rgba.b = 0.85*B + 0.2*rgba.b;\n"  //+ 0.2*rgba.g;
//			Apha
            + "	rgba.a = 1.0;\n"

            
		    + "  gl_FragColor = rgba;\n"
            + "}\n";
	
	GaussianBlurFilter mBlurFilter;
	private GLFrame mBlurFrame;
//	private GLFrame mBlurFrame1;
	private int mWidth;
	private int mHeight;
	
	public ABColorFilter(FilterContext context) {
		super(context, mABColorShader);
		
		mBlurFilter = new GaussianBlurFilter(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void process(Frame[] inputs, Frame output) {
		
        int width = output.getFormat().getWidth();
        int height = output.getFormat().getHeight();
        
        if(mWidth != width || mHeight != height) {
	    	FrameFormat format = ImageFormat.create(width/4,height/4,
	                ImageFormat.COLORSPACE_RGBA,FrameFormat.TARGET_GPU);
	    	if(mBlurFrame != null)
	    		mBlurFrame.releaseNativeAllocation();
	    	mBlurFrame = new GLFrame(format,null,GLFrame.NO_BINDING,0);
	    	mBlurFrame.init(mContext.getGLEnvironment());
	    	
	    	mWidth = width;
	    	mHeight = height;
        }
        mBlurFilter.process(inputs, mBlurFrame);
        
        Frame[] inputlist = {inputs[0],mBlurFrame};
		super.process(inputlist, output);
		
	}
	 @Override
	    public void finalize() {
	    	if(mBlurFrame != null)
	    		mBlurFrame.releaseNativeAllocation();
	    	mBlurFrame = null;
	    	if(mBlurFilter != null)
	    	mBlurFilter.finalize();
	    	mBlurFilter = null;
	        super.finalize();
	    }
   
}

