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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

/**
 *
 * @author MikeUCFL
 */
public class VersionCheck {

    private static final String CURRENT_VERSION = "06122011";
    private static final Logger logger = Logger.getLogger(VersionCheck.class.getName());

    public static String getLatestVersion() {
                
        // Setup HTTP parameters
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.useragent", "iPhoneStalker/" + CURRENT_VERSION);
        HttpClient client = new DefaultHttpClient(params);

        // Construct the post
        HttpGet get = new HttpGet("http://iphonestalker.googlecode.com/svn/wiki/latest_version.txt");
        get.setHeader(new BasicHeader("Content-Type", "text/plain"));

        HttpResponse response = null;
        try {
            // Execute
            response = client.execute(get);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

            String line = reader.readLine();
            if (!CURRENT_VERSION.equals(line)) {
                return line;
            }
        } catch (ClientProtocolException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}
