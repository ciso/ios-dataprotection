/*
 *  This file is a part of iPhoneStalker.
 * 
 *  iPhoneStalker is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.iaik.io;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author MikeUCFL
 */
public class MBUtils {
    
    public static int offset = 0;
    
    public static String getString(DataInputStream dis) throws IOException {
        String value = null;
            
        int length = dis.readUnsignedShort();
        offset += 2;
        if (length > 0 && length < 65535) {
            byte[] data = new byte[length];

            dis.read(data);
            value = new String(data);
            offset += length;
        }
            
        return value;
    }
}
