package com.aemis.promiseanendah.advancedscientificcalculator;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Promise Anendah on 7/2/2018.
 * Saves a variable amount of data stored in a bundle instance to a file
 * The file the data is saved to is private to the app
 */

public class BundleSerializer {

    private static final String TAG = "Bundle_Serializer";

    //the name of the file the data is saved to
    private String filename;
    //the instance bundle that contains the data to save
    private Bundle instanceBundle;
    //the keys used to access data from the bundle
    private Map<String, String> keys;
    private Context appContext;

    /**
     * Create a new bundle serializer
     * @param instanceBundle bundle contains the data that needs to be saved
     * @param keys is the accessor key for the items that need to saved from the bundle instance
     * @param
     */
    public BundleSerializer(Bundle instanceBundle, String filename, Map<String, String> keys, Context applicationContext)
    {
        this.instanceBundle = instanceBundle;
        this.filename = filename;
        this.keys = keys;
        this.appContext = applicationContext;
    }

    /**
     * Saves the data from the bundle to the file,
     * The data is saved in the order of the keys,
     * The same dictionary of keys should used in saving the data should be the same dictionary used in retrieving the content
     */
    public void saveData()
    {
        FileOutputStream fileWriter = null;
        DataOutputStream dataOutput = null;
        try {
            fileWriter = this.appContext.openFileOutput(this.filename, Context.MODE_PRIVATE);
            dataOutput = new DataOutputStream(new BufferedOutputStream(fileWriter));

            //save a value that will be used to determine whether the dictionary used to save is
            //the same as the one being used to read
            dataOutput.writeInt(this.keys.hashCode());

            for(Iterator<String> it = this.keys.keySet().iterator(); it.hasNext();)
            {
                String key = it.next();
                String dataType = this.keys.get(key);
                switch(dataType)
                {
                    case "String":
                        dataOutput.writeChars(this.instanceBundle.getString(key));
                        Log.d(TAG, "saving string");
                        break;
                    case "Char":
                        dataOutput.writeChar(this.instanceBundle.getChar(key));
                        Log.d(TAG, "saving char");
                        break;
                    case "Int":
                        dataOutput.writeInt(this.instanceBundle.getInt(key));
                        Log.d(TAG, "saving int");
                        break;
                    case "Double":
                        dataOutput.writeDouble(this.instanceBundle.getDouble(key));
                        Log.d(TAG, "saving double");
                        break;
                    case "Float":
                        dataOutput.writeFloat(this.instanceBundle.getFloat(key));
                        Log.d(TAG, "saving float");
                        break;
                    case "Boolean":
                        dataOutput.writeBoolean(this.instanceBundle.getBoolean(key));
                        Log.d(TAG, "saving boolean");
                        break;
                }
            }

        }catch(NullPointerException arg) {

        }catch(IOException arg)
        {

        }finally
        {
            try
            {
                if(dataOutput != null)
                {
                    dataOutput.close();
                }
                if(fileWriter != null)
                {
                    fileWriter.close();
                }
            }catch(IOException arg)
            {
                //some awful happened
            }
        }
    }

    /**
     *
     * @param filename
     * @param keys
     * @return
     */
    public static Bundle getData(String filename, Context appContext, Map<String, String> keys) throws IllegalArgumentException
    {
        Bundle newDataBundle = new Bundle();
        FileInputStream inputStream = null;
        DataInputStream dataInputStream = null;
        try {
            inputStream = appContext.openFileInput(filename);
            dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));

            int mapHashCode = dataInputStream.readInt();
            if(keys.hashCode() != mapHashCode)
            {
                throw new IllegalArgumentException("The map of keys is different from the on used in saving the data");
            }

            for(Iterator<String> it = keys.keySet().iterator(); it.hasNext();)
            {
                String key = it.next();
                String dataType = keys.get(key);
                switch(dataType)
                {
                    case "String":
                        String data = dataInputStream.readUTF();
                        newDataBundle.putString(key, data);
                        Log.d(TAG, "reading string");
                        break;
                    case "Char":
                        newDataBundle.putChar(key, dataInputStream.readChar());
                        Log.d(TAG, "reading character");
                        break;
                    case "Int":
                        newDataBundle.putInt(key, dataInputStream.readInt());
                        Log.d(TAG, "reading int");
                        break;
                    case "Double":
                        newDataBundle.putDouble(key, dataInputStream.readDouble());
                        Log.d(TAG, "reading double");
                        break;
                    case "Float":
                        newDataBundle.putFloat(key, dataInputStream.readFloat());
                        Log.d(TAG, "reading float");
                        break;
                    case "Boolean":
                        newDataBundle.putBoolean(key, dataInputStream.readBoolean());
                        Log.d(TAG, "reading boolean");
                        break;
                }
            }
        }catch(FileNotFoundException arg)
        {
            Log.d(TAG, "The file was not found");
        }catch(IOException arg)
        {
            Log.d(TAG, "An error occurred while attempting to perform a read/write operation");
        }finally
        {
            try
            {
                if(inputStream != null)
                {
                    inputStream.close();
                }
                if(dataInputStream != null)
                {
                    dataInputStream.close();
                }
            }catch(IOException arg)
            {

            }
        }
        return newDataBundle;
    }

}
