/*
 *  This file was taken from the iPhoneStalker application.
 *  It has been improved and edited by Stromberger in 2012 of
 *  Graz University of Technology.
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author MikeUCFL
 */
public class MBDBReader {

	private static final Logger logger = Logger.getLogger(MBDBReader.class
			.getName());

	public List<MBDBData> mbdbList = null;

	public MBDBReader() {
		mbdbList = new ArrayList<MBDBData>();
	}

	public boolean processMbdb(File file) {

		// Get the file input stream
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException ex) {
			// logger.log(Level.SEVERE, null, ex);
			return false;
		}

		// Set up the data input stream for reading
		DataInputStream dis = new DataInputStream(fis);

		try {

			// Check the tag
			byte[] tag = new byte[4];
			dis.read(tag);

			MBUtils.offset = 4;
			if (new String(tag).equals("mbdb")) {
				dis.skipBytes(2); // 0x05 0x00, not sure what this is
				MBUtils.offset += 2;

				while (dis.available() > 0) {

					MBDBData mbdbData = new MBDBData(dis);
					mbdbList.add(mbdbData);

					// if (mbdbData.filename != null &&
					// mbdbData.filename.contains("consolid")) {
					// System.out.println(mbdbData);
					// }
				}
			} else {
				logger.log(Level.ALL, "{0} is not a MBDB file!", file);
				return false;
			}

		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
			return false;
		}

		// sorting
		Collections.sort(mbdbList, new CustomComparator());

		return true;
	}

	public class CustomComparator implements Comparator<MBDBData> {
		@Override
		public int compare(MBDBData o1, MBDBData o2) {
			int comp = o1.domain.compareTo(o2.domain);
			if (comp == 0) {
				if (o1.filename != null && o2.filename != null) {
					comp = o1.filename.compareTo(o2.filename);
				}
			}
			return comp;
		}
	}

	public class MBDBData {
		public int startOffset = -1;
		public String domain = null;
		public String filename = null;
		public String linkTarget = null;
		public String dataHash = null;
		public String unknown1 = null;
		public int mode = -1;
		public int unknown2 = -1;
		public int unknown3 = -1;
		public int userId = -1;
		public int groupId = -1;
		public int mtime = -1;
		public int atime = -1;
		public int ctime = -1;
		public long fileLength = -1;
		public int flag = -1;
		public int numProperties = -1;
		public ArrayList<MBDBProperties> properties = new ArrayList<MBDBProperties>();

		MBDBData(DataInputStream dis) throws IOException {
			this.startOffset = MBUtils.offset;
			domain = MBUtils.getString(dis);
			filename = MBUtils.getString(dis);
			linkTarget = MBUtils.getString(dis);
			dataHash = MBUtils.getString(dis);
			unknown1 = MBUtils.getString(dis);
			mode = dis.readUnsignedShort();
			MBUtils.offset += 2;
			unknown2 = dis.readInt();
			MBUtils.offset += 4;
			unknown3 = dis.readInt();
			MBUtils.offset += 4;
			userId = dis.readInt();
			MBUtils.offset += 4;
			groupId = dis.readInt();
			MBUtils.offset += 4;
			mtime = dis.readInt();
			MBUtils.offset += 4;
			atime = dis.readInt();
			MBUtils.offset += 4;
			ctime = dis.readInt();
			MBUtils.offset += 4;
			fileLength = dis.readLong();
			MBUtils.offset += 8;
			flag = dis.readUnsignedByte();
			MBUtils.offset += 1;
			numProperties = dis.readUnsignedByte();
			MBUtils.offset += 1;

			for (int i = 0; i < numProperties; i++) {
				MBDBProperties mbdbProperties = new MBDBProperties(dis);
				properties.add(mbdbProperties);
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("startOffset: ");
			sb.append(startOffset);
			sb.append("\n");

			sb.append("domain: ");
			sb.append(domain);
			sb.append("\n");
			sb.append("filename: ");
			sb.append(filename);
			sb.append("\n");
			sb.append("linkTarget: ");
			sb.append(linkTarget);
			sb.append("\n");

			sb.append("dataHash: ");
			sb.append(dataHash);
			sb.append("\n");

			sb.append("unknown1: ");
			sb.append(unknown1);
			sb.append("\n");

			sb.append("mode: ");
			sb.append(mode);
			sb.append("\n");

			sb.append("unknown2: ");
			sb.append(unknown2);
			sb.append("\n");
			sb.append("unknown3: ");
			sb.append(unknown3);
			sb.append("\n");

			sb.append("userId: ");
			sb.append(userId);
			sb.append("\n");
			sb.append("groupId: ");
			sb.append(groupId);
			sb.append("\n");

			sb.append("mtime: ");
			sb.append(mtime);
			sb.append("\n");
			sb.append("atime: ");
			sb.append(atime);
			sb.append("\n");
			sb.append("ctime: ");
			sb.append(ctime);
			sb.append("\n");

			sb.append("fileLength: ");
			sb.append(fileLength);
			sb.append("\n");
			sb.append("flag: ");
			sb.append(flag);
			sb.append("\n");

			sb.append("numProperties: ");
			sb.append(numProperties);
			sb.append("\n");
			for (int i = 0; i < numProperties; i++) {
				sb.append(properties.get(i));
			}

			return sb.toString();
		}
	}

	private class MBDBProperties {
		String propertyName = null;
		String propertyValue = null;

		MBDBProperties(DataInputStream dis) throws IOException {
			propertyName = MBUtils.getString(dis);
			propertyValue = MBUtils.getString(dis);
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("propertyName: ");
			sb.append(propertyName);
			sb.append("\n");
			sb.append("propertyValue: ");
			sb.append(propertyValue);
			sb.append("\n");

			return sb.toString();
		}
	}
}
