package ark.dock.geo.las;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import dust.gen.DustGenLog;

public class ArkDockGeoLasReader {
	
	public static void main(String[] args) throws Exception {
		String fName = args[0];
		DustGenLog.log("Reading", fName);

		byte[] data = new byte[400];
		try (FileInputStream in = new FileInputStream(fName)) {
			if ( in.read(data, 0, 400) < 400 )
				throw new Exception("unable to read file contents");

			ByteBuffer buf = ByteBuffer.wrap(data);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			
			byte[] chBuf = new byte[40];
			
			buf.get(chBuf, 0, 4);
			
			DustGenLog.log("Signature", new String(chBuf));
			
			DustGenLog.log("Source ID ", buf.getShort());
			DustGenLog.log("Global Encoding", buf.getShort());
			DustGenLog.log("Project ID - GUID Data 1", buf.getInt());
			DustGenLog.log("Project ID - GUID Data 2", buf.getShort());
			DustGenLog.log("Project ID - GUID Data 3", buf.getShort());
			buf.get(chBuf, 0, 8);
			DustGenLog.log("Project ID - GUID Data 4", new String(chBuf));
			DustGenLog.log("Version Major", buf.get());
			DustGenLog.log("Version Minor", buf.get());
			buf.get(chBuf, 0, 32);
			DustGenLog.log("System Identifier", new String(chBuf));
			buf.get(chBuf, 0, 32);
			DustGenLog.log("Generating Software", new String(chBuf));
			DustGenLog.log("File Creation Day of Year", buf.getShort());
			DustGenLog.log("File Creation Year", buf.getShort());
			DustGenLog.log("Header Size", buf.getShort());
			DustGenLog.log("Offset to Point Data", buf.getInt());
			DustGenLog.log("Number of Variable Length Records", buf.getInt());
			DustGenLog.log("Point Data Record Format", buf.get());

			DustGenLog.log("Point Data Record Length", buf.getShort());
			DustGenLog.log("Legacy Number of Point Records", buf.getInt());
			
			for ( int i = 0; i < 5; ++i ) {
				DustGenLog.log("Legacy Number of Point by Return", i, buf.getInt());
			}
			
			DustGenLog.log("X Scale Factor", buf.getDouble());
			DustGenLog.log("Y Scale Factor", buf.getDouble());
			DustGenLog.log("Z Scale Factor", buf.getDouble());
			DustGenLog.log("X Offset", buf.getDouble());
			DustGenLog.log("Y Offset", buf.getDouble());
			DustGenLog.log("Z Offset", buf.getDouble());
			DustGenLog.log("Max X", buf.getDouble());
			DustGenLog.log("Min X", buf.getDouble());
			DustGenLog.log("Max Y", buf.getDouble());
			DustGenLog.log("Min Y", buf.getDouble());
			DustGenLog.log("Max Z", buf.getDouble());
			DustGenLog.log("Min Z", buf.getDouble());
			
			/*
File Source ID 	unsigned short	2
Global Encoding 	unsigned short	2
Project ID - GUID Data 1 	unsigned long	4
Project ID - GUID Data 2 	unsigned short	2
Project ID - GUID Data 3 	unsigned short	2
Project ID - GUID Data 4 	unsigned char[8]	8
Version Major 	unsigned char	1
Version Minor 	unsigned char	1
System Identifier 	char[32]	32
Generating Software 	char[32]	32
File Creation Day of Year 	unsigned short	2
File Creation Year 	unsigned short	2
Header Size 	unsigned short	2
Offset to Point Data 	unsigned long	4
Number of Variable Length Records 	unsigned long	4
Point Data Record Format 	unsigned char	1
Point Data Record Length 	unsigned short	2
Legacy Number of Point Records 	unsigned long	4
Legacy Number of Point by Return 	unsigned long[5]	20
X Scale Factor 	double	8
Y Scale Factor 	double	8
Z Scale Factor 	double	8
X Offset 	double	8
Y Offset 	double	8
Z Offset 	double	8
Max X 	double	8
Min X 	double	8
Max Y 	double	8
Min Y 	double	8
Max Z 	double	8
Min Z 	double	8
Start of Waveform Data Packet Record 	unsigned long long	8
Start of First Extended Variable Length Record 	unsigned long long	8
Number of Extended Variable Length Records 	unsigned long	4
Number of Point Records	unsigned long long	8
Number of Points by Return 	unsigned long long[15] 	120
			 */
		}
	}
}
