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
package iphonestalker.util.io;

import iphonestalker.data.IPhoneRoute;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author MikeUCFL
 */
public class InfoReader extends DefaultHandler {

    private static final Logger logger =
            Logger.getLogger(InfoReader.class.getName());
    
    private String tempVal = null;
    private IPhoneRoute iPhoneData = null;
    private boolean readDisplay = false;
    private boolean readBackupDate = false;

    public InfoReader() {
    }
    
    public IPhoneRoute parseFile(File file) {
        try {
            //get a factory
            SAXParserFactory spf = SAXParserFactory.newInstance();

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            iPhoneData = new IPhoneRoute();
            
            //parse the file and also register this class for call backs
            sp.parse(file, this);
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(InfoReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(InfoReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InfoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return iPhoneData;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName,
            String qName) throws SAXException {

        if (readDisplay) {
            iPhoneData.name = tempVal;
            readDisplay = false;
        } else if (readBackupDate) {
            
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z' z");
            try {
                tempVal += " GMT-00:00";
                cal.setTime(sdf.parse(tempVal));
                iPhoneData.lastBackupDate = cal.getTime();
            } catch (ParseException ex) {
                logger.log(Level.WARNING, "Couldn't set iPhone backup date", ex);
                iPhoneData.lastBackupDate = null;
            }
            readBackupDate = false;
        } else if (tempVal.equals("Display Name")) {
            readDisplay = true;
        } else if (tempVal.equals("Last Backup Date")) {
            readBackupDate = true;
        }
    }
 
}