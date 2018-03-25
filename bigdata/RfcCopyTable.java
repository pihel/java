package jco;
 
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
 
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
 
 
class Config {
       static String JCO_ASHOST = "10.0.0.11";
       static String JCO_SYSNR = "04"; //port
       static String JCO_CLIENT = "500";
       static String JCO_USER = "HADOOP_RFC";
       static String JCO_PASSWD = "";
       static String JCO_LANG = "en";
      
       static String destName = "ABAP_AS";
      
       static String RFC_FUNC = "/SAPDS/RFC_READ_TABLE";
       static String DELIMITER = "\t";
       static int ROWSKIPS = 0;
       static int ROWCOUNT = 50000;
      
       static String QUERY_TABLE = "EKBE";
       static String WHERE = "MANDT = '500'";
       static String FIELDNAMES[] = null;
      
       static void setFieldName(String columns) {
             if(columns.equals("*")) {
                    FIELDNAMES = null;
             }
             FIELDNAMES = columns.split(",");
             for(int i = 0; i < FIELDNAMES.length; i++) {
                    FIELDNAMES[i] = FIELDNAMES[i].trim();
             }
       }
} //Config
 
public class jco
{
    static class MyDestinationDataProvider implements DestinationDataProvider
    {
      
        private DestinationDataEventListener eL;
        private HashMap<String, Properties> secureDBStorage = new HashMap<String, Properties>();
       
        public Properties getDestinationProperties(String destinationName)
        {
            try
            {
                //read the destination from DB
                Properties p = secureDBStorage.get(destinationName);
 
                if(p!=null)
                {
                    //check if all is correct, for example
                    if(p.isEmpty())
                        throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION, "destination configuration is incorrect", null);
 
                    return p;
                }
               
                return null;
            }
            catch(RuntimeException re)
            {
                throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR, re);
            }
        }
 
        //An implementation supporting events has to retain the eventListener instance provided
        //by the JCo runtime. This listener instance shall be used to notify the JCo runtime
        //about all changes in destination configurations.
        public void setDestinationDataEventListener(DestinationDataEventListener eventListener)
        {
            this.eL = eventListener;
        }
 
        public boolean supportsEvents()
        {
            return true;
        }
 
        //implementation that saves the properties in a very secure way
        void changeProperties(Properties properties)
        {
            synchronized(secureDBStorage)
            {
                if(properties==null)
                {
                    if(secureDBStorage.remove(Config.destName)!=null)
                        eL.deleted(Config.destName);
                }
                else
                {
                    secureDBStorage.put(Config.destName, properties);
                    eL.updated(Config.destName); // create or updated
                }
            }
        }
    } // end of MyDestinationDataProvider
   
    //business logic
    void executeCalls()
    {
        JCoDestination destination;
        try
        {
             destination = JCoDestinationManager.getDestination(Config.destName);
           
            JCoFunction function = destination.getRepository().getFunction(Config.RFC_FUNC);
            if(function == null) {
                throw new RuntimeException(Config.RFC_FUNC + " not found in SAP.");
            }
           
            JCoParameterList listParams = function.getImportParameterList();
           
            //selected table
            listParams.setValue("QUERY_TABLE", Config.QUERY_TABLE);
            listParams.setValue("DELIMITER", Config.DELIMITER);
            listParams.setValue("ROWSKIPS", Integer.valueOf(Config.ROWSKIPS));
            listParams.setValue("ROWCOUNT", Integer.valueOf(Config.ROWCOUNT));
           
            //where cond
            if(Config.WHERE != null && !Config.WHERE.isEmpty()) {
                   JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
                   returnOptions.appendRow();
                   returnOptions.setValue("TEXT", Config.WHERE);
            }
           
            //select columns
            if(Config.FIELDNAMES != null && Config.FIELDNAMES.length > 0) {
                   JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");
                   for(int i = 0; i < Config.FIELDNAMES.length; i++) {
                         returnFields.appendRow();
                         returnFields.setValue("FIELDNAME", Config.FIELDNAMES[i]);
                   }
            }
           
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");           
 
            try {                
             System.out.println("Start execute rfc " + dateFormat.format(new Date()));             
                function.execute(destination);               
                System.out.println("End execute rfc " + dateFormat.format(new Date()));
            }
            catch(AbapException e) {
                System.out.println(e.toString());
                return;
            }
           
            System.out.println("Start getTable " + dateFormat.format(new Date()));           
            JCoTable tableList = function.getTableParameterList().getTable("DATA");           
            System.out.println("End getTable " + dateFormat.format(new Date()));
           
            String line="";
           
            int num_rows = 0;
            int num_delim = 10;
            if (tableList.getNumRows() > 0) {
                    FileWriter fw = new FileWriter(Config.QUERY_TABLE+".txt");
                    do {
                       line=tableList.getString(0);
                       //System.out.println(line);
                       fw.write(line);
                       fw.write("\n");
                       
                       num_rows++;
                       
                       if(num_rows%num_delim == 0) {
                              System.out.println(num_rows);
                              num_delim = num_delim *10;
                       }
                    }
                    while (tableList.nextRow() == true);
                    System.out.println(num_rows);
                    fw.close();
            }
        }
        catch (IOException e) {
                    e.printStackTrace();
             }
        catch(JCoException e) {
            e.printStackTrace();
        }
    }
   
    static Properties getDestinationPropertiesFromUI() {
        //adapt parameters in order to configure a valid destination
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, Config.JCO_ASHOST);
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  Config.JCO_SYSNR);
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, Config.JCO_CLIENT);
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   Config.JCO_USER);
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, Config.JCO_PASSWD);
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   Config.JCO_LANG);
       
        return connectProperties;
    }
   
    public static void main(String[] args) {
      
       DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 
       System.out.println("Start " + dateFormat.format(new Date()));
        MyDestinationDataProvider myProvider = new MyDestinationDataProvider();
       
        //register the provider with the JCo environment;
        //catch IllegalStateException if an instance is already registered
        try {
            com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(myProvider);
        }
        catch(IllegalStateException providerAlreadyRegisteredException) {
            //somebody else registered its implementation,
            //stop the execution
            throw new Error(providerAlreadyRegisteredException);
        }
       
        
        Config.setFieldName("MANDT, EBELN, EBELP, VGABE, BELNR, BUZEI, BEWTP, BWART, BUDAT, XBLNR");
        jco test = new jco();
       
        //set properties for the destination and ...
        myProvider.changeProperties(getDestinationPropertiesFromUI());
        //... work with it
        test.executeCalls();
       
        System.out.println("End " + dateFormat.format(new Date()));
    }
   
}