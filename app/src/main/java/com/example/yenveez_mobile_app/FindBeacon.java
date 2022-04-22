package com.example.yenveez_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kkmcn.kbeaconlib2.KBeacon;
import com.kkmcn.kbeaconlib2.KBeaconsMgr;

public class FindBeacon extends AppCompatActivity implements KBeaconsMgr.KBeaconMgrDelegate {

    Button scanBeacon,stopScan;
    TextView beaconScanStatusText;
    ProgressBar scanBeaconProgressBar;
    ListView listViewBeacon;
    ListAdapter adapter;
    private KBeaconsMgr mBeaconsMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_beacon);

        //Checking Beacon
        mBeaconsMgr = KBeaconsMgr.sharedBeaconManager(this);
        if (mBeaconsMgr == null)
        {
            Toast.makeText(this, "Make sure the phone has support ble function", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mBeaconsMgr.delegate = this;
        mBeaconsMgr.setScanMinRssiFilter(-40);
        mBeaconsMgr.setScanMode(KBeaconsMgr.SCAN_MODE_LOW_LATENCY);


        //Defining references
        beaconScanStatusText = findViewById(R.id.BeaconScanStatusText);
        scanBeaconProgressBar = findViewById(R.id.ScanBeaconProgressBar);
        listViewBeacon = findViewById(R.id.ListViewBeacon);
        scanBeacon = findViewById(R.id.ScanBeacon);
        stopScan = findViewById(R.id.StopScan);

        //Start Scan button OnClick
        scanBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartScanUIUpdate();
            }
        });

        //Stop Scan button OnClick
        stopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopScanUIUpdate();
            }
        });
    }

    //UI update onClick start scan
    private void StartScanUIUpdate(){
        scanBeacon.setVisibility(View.GONE);
        stopScan.setVisibility(View.VISIBLE);
        beaconScanStatusText.setVisibility(View.VISIBLE);
        beaconScanStatusText.setText("Scanning...");
        scanBeaconProgressBar.setVisibility(View.VISIBLE);
    }

    //UI update onClick stop scan
    private void StopScanUIUpdate(){
        stopScan.setVisibility(View.GONE);
        scanBeacon.setVisibility(View.VISIBLE);
        beaconScanStatusText.setText("Stopped");
        scanBeaconProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBeaconDiscovered(KBeacon[] beacons) {

    }

    @Override
    public void onCentralBleStateChang(int nNewState) {

    }

    @Override
    public void onScanFailed(int errorCode) {

    }
}