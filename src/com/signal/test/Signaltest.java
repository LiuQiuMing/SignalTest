package com.signal.test;


import java.io.File;
import java.io.FileInputStream;

import org.apache.http.util.EncodingUtils;

import com.signal.test.Cell;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;






public class Signaltest extends Activity 
{
	
	View				mMainView;
	View				mDatabaseView;
 TelephonyManager        Tel;
 MyPhoneStateListener    MyListener;
 TextView				 mtvRx,mtvEcIo,mtvRxDO,mtvSNRDO,mtvCI;
 int					 mRxInt,mEcIoInt,mRxDOInt,mSNRDOInt,mCIdInt,mPNInt,mSIdInt,mNIdInt;
 TextView                mtvCId,mtvSID,mtvNID,mtvPN,mtvCellName;

 AlertDialog.Builder 	 mAbout;
 ProgressDialog			 mProDialog;
 CdmaCellLocation 		 cdma;
 int 					 mPhoneType;
 boolean				 mState;
 
 SQLiteDatabase          mDB;
 
 protected static final int MENU_ABOUT = Menu.FIRST;
 protected static final int MENU_CreateDB = Menu.FIRST+1;
 protected static final int MENU_Quit  = Menu.FIRST+2;
 
 @Override
 public boolean onCreateOptionsMenu(Menu menu)
 {
	 super.onCreateOptionsMenu(menu);
	 menu.add(0,MENU_ABOUT,0,"关于...");
	 menu.add(0,MENU_CreateDB,0,"创建小区数据库");
	 menu.add(0, MENU_Quit, 0, "退出");
	 return true;
 }
 @Override
 public boolean onPrepareOptionsMenu(Menu menu)
 {
	 if(mState) 
	 {
	 super.onPrepareOptionsMenu(menu);
	 
	 return true;
	 }else return false;
 }
 @Override
public boolean onOptionsItemSelected(MenuItem item)
{
	super.onOptionsItemSelected(item);
	switch(item.getItemId())
	{
	case MENU_ABOUT:
		openOptionsDialog();
		break;
	case MENU_CreateDB:
		setContentView(mDatabaseView);		
		mState = false;
		break;
	case MENU_Quit:
		finish();
		break;
	}
	
	return true;
	
}

public void openOptionsDialog()
{
	mAbout = new AlertDialog.Builder(this).setTitle("关于本程序").setMessage("LiuQiuMing 2011\n\nLiuQM@gdtel.com.cn"); 
	mAbout.show();
}
 /** Called when the activity is first created. */

  @Override

  public void onCreate(Bundle savedInstanceState)
  {
      super.onCreate(savedInstanceState);
      
      LayoutInflater inFlater = getLayoutInflater();
      mMainView = inFlater.inflate(R.layout.main, null);
      mDatabaseView = inFlater.inflate(R.layout.database, null);
      
      setContentView(mMainView);
      
     // setContentView(R.layout.main);
      mState = true;
  
      Log.d("SignalTest","onCreate()");
      //初始化数据库
 
 

      //打开或创建test.db数据库  
      mDB = openOrCreateDatabase("cells.db", Context.MODE_PRIVATE, null);

      mtvRx = (TextView)findViewById(R.id.RxPower);
      mtvRx.setTextSize(20);
      mtvEcIo= (TextView)findViewById(R.id.EcIo);
      mtvEcIo.setTextSize(20);
      mtvRxDO= (TextView)findViewById(R.id.RxDO);
      mtvRxDO.setTextSize(20);
      mtvSNRDO= (TextView)findViewById(R.id.SNRDO);
      mtvSNRDO.setTextSize(20);
      mtvCId = (TextView)findViewById(R.id.CellId);
      mtvCId.setTextSize(20);
      mtvSID = (TextView)findViewById(R.id.SID);
      mtvSID.setTextSize(20);
      mtvNID = (TextView)findViewById(R.id.NID);
      mtvNID.setTextSize(20);
      mtvPN = (TextView)findViewById(R.id.PN);
      mtvPN.setTextSize(20);
      mtvCellName = (TextView)findViewById(R.id.CellName);
      mtvCellName.setTextSize(20);
      
      Tel       = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);  
      MyListener   = new MyPhoneStateListener();
      mPhoneType = Tel.getPhoneType();
     

  }



  /* Called when the application is minimized */

  @Override

 protected void onPause()

  {

    super.onPause();
    Log.d("SignalTesl","onPause()");
    Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);

 }



  /* Called when the application resumes */

 @Override

 protected void onResume()

 {

    super.onResume();
    
    Log.d("SignalTest","onResume()");   
    

    
    /* Update the listener, and start it */
  
   Tel.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
   
   if(mPhoneType!=TelephonyManager.PHONE_TYPE_CDMA)
   {
  	  Toast.makeText(getApplicationContext(), "本应用仅支持电信CDMA手机",Toast.LENGTH_LONG).show();
 	 
    }
    

 }
 //读SD中的文件
public String readFileSdcardFile(String fileName)
{ 
 String res=""; 
 try{ 
        FileInputStream fin = new FileInputStream(fileName); 

        int length = fin.available(); 

        byte [] buffer = new byte[length]; 
        fin.read(buffer);     

        res = EncodingUtils.getString(buffer, "UTF-8"); 

        fin.close();     
       } 

       catch(Exception e){ 
        e.printStackTrace(); 
       } 

       return res; 
} 
public void onReturn(View view)
{
	setContentView(mMainView);
	mState = true;
}
 public void onClick(View view)
 {
	 mProDialog = new ProgressDialog(Signaltest.this);
	 mProDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	 mProDialog.setTitle("重构小区数据");
	 mProDialog.setMessage("Please wait...");
	 mProDialog.setIndeterminate(false);
	 mProDialog.show(); 
	 
	  
	 
	 
	 
	 //删除表格
	 mDB.execSQL("DROP TABLE IF EXISTS cells");  
     //创建cells表  
     mDB.execSQL("CREATE TABLE cells (_id INTEGER PRIMARY KEY AUTOINCREMENT, CI VARCHAR,cell_name VARCHAR, PN INTEGER)");  
     
     //读取csv文件
    String file = Environment.getExternalStorageDirectory().getPath()+"/cells.csv";    
    String strRes = readFileSdcardFile(file);    
    final String[] strCells= strRes.split("\\n");
 
    new Thread(){
    	public void run()
    	{
    		int len = strCells.length;
    		mProDialog.setMax(len);
    		mProDialog.setProgress(0);
    		
    		Cell mCell = new Cell();
    	     for(int i =0; i<len; i++)
    	     {   	 
    	    	 String[] strCell =strCells[i].split(",");
    	    	
    	    	 String strPn = strCell[1];      	 
    	         mCell.cell_name =strCell[2];
    	         mCell.CI = strCell[0];
    	    	 mCell.PN = Integer.parseInt(strPn);	
    	    	 //按文件每行插入数据表
    	    	 mDB.execSQL("INSERT INTO cells VALUES (NULL, ?, ? , ?)", new Object[]{mCell.CI, mCell.cell_name,mCell.PN});
    	    	mProDialog.incrementProgressBy(1); 
    	     }
    	     mProDialog.dismiss();
    	}
    	
    	
    }.start();
    
     

	
 }
 @Override
 protected void onDestroy() 
 {
 	super.onDestroy();
 	//应用的最后一个Activity关闭时应释放DB
 	Log.d("SinalTest","onDestroy()");
 	mDB.close();
 }
 


  /* Start the PhoneState listener */

  private class MyPhoneStateListener extends PhoneStateListener

  {

    /* Get the Signal strength from the provider, each time there is an update */
	final int COLORTYPE_SIG=0,COLORTYPE_ECIO=1;
    @Override

    public void onSignalStrengthsChanged(SignalStrength signalStrength)

    {
 
       super.onSignalStrengthsChanged(signalStrength);
    //   Toast.makeText(getApplication(), "onSignalStrengthsChanged", Toast.LENGTH_SHORT);
       
       if(mPhoneType == TelephonyManager.PHONE_TYPE_CDMA)
       {
       mRxInt = signalStrength.getCdmaDbm();
       mtvRx.setTextColor(getColor(COLORTYPE_SIG,mRxInt));
       
       String strRx = Integer.toString(mRxInt);
       mtvRx.setText(strRx+"dBm");
       
       
       mEcIoInt = signalStrength.getCdmaEcio()/10;
       mtvEcIo.setTextColor(getColor(COLORTYPE_ECIO,mEcIoInt));
       
       String strEcIo = Integer.toString(mEcIoInt);       
       mtvEcIo.setText(strEcIo+"dB");
       
       mRxDOInt = signalStrength.getEvdoDbm();
       mtvRxDO.setTextColor(getColor(COLORTYPE_SIG,mRxDOInt));
       
       String strRxDO = Integer.toString(mRxDOInt);
       mtvRxDO.setText(strRxDO+"dBm"); 
       
       mSNRDOInt = signalStrength.getEvdoSnr();
       mtvSNRDO.setTextColor(getColor(COLORTYPE_ECIO,mSNRDOInt));
       
       String strSNRDO = Integer.toString(mSNRDOInt);  
       mtvSNRDO.setText(strSNRDO+"dB");
       
       cdma = (CdmaCellLocation)Tel.getCellLocation();
       mCIdInt = cdma.getBaseStationId();
  
       mSIdInt = cdma.getSystemId();
       mNIdInt = cdma.getNetworkId();
      
       mtvCId.setText("CI:"+Integer.toString(mCIdInt));
       mtvSID.setText("SID:"+Integer.toString(mSIdInt));
       mtvNID.setText("NID:"+Integer.toString(mNIdInt));
       
       
       Cell mCell = new Cell();
       String strCi = Integer.toString(mCIdInt);
       Cursor c = mDB.rawQuery("SELECT * FROM cells WHERE ci = ? ", new String[]{strCi}); 
      if( c.moveToNext())
      {
    	  mCell.CI = c.getString(c.getColumnIndex("CI"));
    	  mCell.PN = c.getInt(c.getColumnIndex("PN"));
    	  mCell.cell_name = c.getString(c.getColumnIndex("cell_name"));
          mtvPN.setText("PN:"+String.valueOf(mCell.PN));
          mtvCellName.setText("小区："+mCell.cell_name);    
          c.close();
      }else{
    	  mtvCellName.setText("未找到小区");
      }
      }     
       
  
    }
private int getColor(int colorType,int value)
{
	int iColor = 0xffffffff;
	switch(colorType)
	{
		case COLORTYPE_SIG:
		{
			if(value >=-75){iColor = 0xff00ff00;} //green
			else if(value >=-85){iColor =0xff0000ff;} //blue
			else if(value >=-90){iColor =0xffffff00;} //yellow
			else {iColor = 0xffff0000;}               //red
			break;
		}
		case COLORTYPE_ECIO:
		{
			if(value >=-5){iColor = 0xff00ff00;} //green
			else if(value >=-8){iColor =0xff0000ff;} //blue
			else if(value >=-12){iColor =0xffffff00;} //yellow
			else {iColor = 0xffff0000;}               //red
			break;
		}
	}
	return iColor;
}


  };/* End of private Class */



}/* SignalTest */

