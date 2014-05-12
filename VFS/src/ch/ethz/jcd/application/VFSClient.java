package ch.ethz.jcd.application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class VFSClient
{
    public static void main(String argv[]) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Socket client = new Socket("localhost", 2000);
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        while(true)
        {
            String line = reader.readLine();
            out.writeBytes(line+"\n");
            String input = in.readLine();

            while(!input.equals("\0"))
            {
                System.out.println(input);
                input = in.readLine();
            }

            if(line.equals("quit"))
            {
                break;
            }
        }
        client.close();
    }
}
