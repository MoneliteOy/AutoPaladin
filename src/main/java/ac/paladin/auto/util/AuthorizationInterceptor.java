/*
 *  Copyright (c) 2021, Paladin.ac
 *
 *  All rights reserved.
 *
 *  Author(s):
 *   Marshall Walker
 */

package ac.paladin.auto.util;

import ac.paladin.auto.config.PluginConfig;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@RequiredArgsConstructor
public final class AuthorizationInterceptor implements Interceptor {

    private final PluginConfig i_pluginConfig;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request request = original.newBuilder()
                .header("Authorization", i_pluginConfig.getApiKey())
                .build();

        return chain.proceed(request);
    }
}
