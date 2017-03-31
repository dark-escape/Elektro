package com.stonecode.elektro;

import java.util.ArrayList;

/**
 * Created by vishal on 3/24/17.
 */

public interface FinishScanListener {
    /**
     * Interface called when the scan method finishes. Network operations should not execute on UI thread
     * @param  {@link ClientScanResult}
     */

    public void onFinishScan(ArrayList<ClientScanResult> clients);
}
