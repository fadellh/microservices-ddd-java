/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.mwc.kafka.order.avro.model;
@org.apache.avro.specific.AvroGenerated
public enum OrderStatus implements org.apache.avro.generic.GenericEnumSymbol<OrderStatus> {
  AWAITING_PAYMENT, REVIEW_PAYMENT, APPROVED, CANCEL_PENDING, CANCELLED  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"OrderStatus\",\"namespace\":\"com.mwc.kafka.order.avro.model\",\"symbols\":[\"AWAITING_PAYMENT\",\"REVIEW_PAYMENT\",\"APPROVED\",\"CANCEL_PENDING\",\"CANCELLED\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
