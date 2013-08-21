
package com.shephertz.image.cahing;

import java.io.InputStream;
import java.io.OutputStream;


/*
 * @author Vishnu Garg
 */
public class Utility {
	
	/*
	 * This function copy InputStream bytes to OutputStream bytes
	 * @param InputStream
	 * @param OutputStream
	 */
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}


