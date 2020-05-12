/*
 * Copyright (C) 2013 Sebastian Kaspari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androidzeitgeist.ani.internal;

/**
 * Internal class for implementation specific constants.
 */
public class AndroidNetworkIntents {
    //-----组播------
    public static final String DISCOVERY_ADDRESS = "10.200.12.64";//监听的组播地址

    //监听消息端口
    public static final int DISCOVERY_PORT = 8888;

//    public static final int TRANSMITTER_NATIVE_PORT = 8888;

    //----------------------

    //-----发送本地端口------

    //发送消息目标端口
    public static final int TRANSMITTER_PORT = 8888;

    //发送消息IP
    public static final String TRANSMITTER_ADDRESS = "10.200.12.64";

}
