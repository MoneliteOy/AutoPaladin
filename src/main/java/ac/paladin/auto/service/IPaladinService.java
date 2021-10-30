/*
 *  Copyright (c) 2021, Paladin.ac
 *
 *  All rights reserved.
 *
 *  Author(s):
 *   Marshall Walker
 */

package ac.paladin.auto.service;

import ac.paladin.auto.model.http.StartScanRequest;
import ac.paladin.auto.model.http.StartScanResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IPaladinService {

    @POST("scan")
    Call<StartScanResponse> startScan(@Body StartScanRequest request);
}
