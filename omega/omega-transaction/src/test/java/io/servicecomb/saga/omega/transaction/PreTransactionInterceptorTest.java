/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.saga.omega.transaction;

import static com.seanyinx.github.unit.scaffolding.Randomness.nextId;
import static com.seanyinx.github.unit.scaffolding.Randomness.uniquify;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class PreTransactionInterceptorTest {
  private final List<byte[]> messages = new ArrayList<>();
  private final long txId = nextId();

  private final MessageSender sender = messages::add;
  private final MessageSerializer serializer = event -> {
    if (event.payloads()[0] instanceof String) {
      String message = (String) event.payloads()[0];
      return serialize(txId, message);
    }
    throw new IllegalArgumentException("Expected instance of String, but was " + event.getClass());
  };

  private final String message = uniquify("message");
  private final PreTransactionInterceptor interceptor = new PreTransactionInterceptor(sender, serializer);

  private byte[] serialize(long txId, String message) {
    return (txId + ":" + message).getBytes();
  }

  @Test
  public void sendsSerializedMessage() throws Exception {
    interceptor.intercept(txId, message);

    assertThat(messages, contains(serialize(txId, message)));
  }
}