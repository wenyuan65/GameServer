// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: packet.proto

package com.panda.game.proto;

public final class PacketPb {
  private PacketPb() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface PkgOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.panda.game.proto.Pkg)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * 请求id
     * </pre>
     *
     * <code>int32 requestId = 1;</code>
     * @return The requestId.
     */
    int getRequestId();

    /**
     * <pre>
     *命令号，区分不同命令
     * </pre>
     *
     * <code>int32 cmd = 2;</code>
     * @return The cmd.
     */
    int getCmd();

    /**
     * <pre>
     *错误码
     * </pre>
     *
     * <code>int32 error_code = 3;</code>
     * @return The errorCode.
     */
    int getErrorCode();

    /**
     * <pre>
     *玩家唯一ID
     * </pre>
     *
     * <code>int64 playerId = 4;</code>
     * @return The playerId.
     */
    long getPlayerId();

    /**
     * <pre>
     *包体的二进制数据
     * </pre>
     *
     * <code>bytes body = 5;</code>
     * @return The body.
     */
    com.google.protobuf.ByteString getBody();
  }
  /**
   * <pre>
   *游戏包数据
   * </pre>
   *
   * Protobuf type {@code com.panda.game.proto.Pkg}
   */
  public static final class Pkg extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:com.panda.game.proto.Pkg)
      PkgOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Pkg.newBuilder() to construct.
    private Pkg(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Pkg() {
      body_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Pkg();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Pkg(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {

              requestId_ = input.readInt32();
              break;
            }
            case 16: {

              cmd_ = input.readInt32();
              break;
            }
            case 24: {

              errorCode_ = input.readInt32();
              break;
            }
            case 32: {

              playerId_ = input.readInt64();
              break;
            }
            case 42: {

              body_ = input.readBytes();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.panda.game.proto.PacketPb.internal_static_com_panda_game_proto_Pkg_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.panda.game.proto.PacketPb.internal_static_com_panda_game_proto_Pkg_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.panda.game.proto.PacketPb.Pkg.class, com.panda.game.proto.PacketPb.Pkg.Builder.class);
    }

    public static final int REQUESTID_FIELD_NUMBER = 1;
    private int requestId_;
    /**
     * <pre>
     * 请求id
     * </pre>
     *
     * <code>int32 requestId = 1;</code>
     * @return The requestId.
     */
    @java.lang.Override
    public int getRequestId() {
      return requestId_;
    }

    public static final int CMD_FIELD_NUMBER = 2;
    private int cmd_;
    /**
     * <pre>
     *命令号，区分不同命令
     * </pre>
     *
     * <code>int32 cmd = 2;</code>
     * @return The cmd.
     */
    @java.lang.Override
    public int getCmd() {
      return cmd_;
    }

    public static final int ERROR_CODE_FIELD_NUMBER = 3;
    private int errorCode_;
    /**
     * <pre>
     *错误码
     * </pre>
     *
     * <code>int32 error_code = 3;</code>
     * @return The errorCode.
     */
    @java.lang.Override
    public int getErrorCode() {
      return errorCode_;
    }

    public static final int PLAYERID_FIELD_NUMBER = 4;
    private long playerId_;
    /**
     * <pre>
     *玩家唯一ID
     * </pre>
     *
     * <code>int64 playerId = 4;</code>
     * @return The playerId.
     */
    @java.lang.Override
    public long getPlayerId() {
      return playerId_;
    }

    public static final int BODY_FIELD_NUMBER = 5;
    private com.google.protobuf.ByteString body_;
    /**
     * <pre>
     *包体的二进制数据
     * </pre>
     *
     * <code>bytes body = 5;</code>
     * @return The body.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getBody() {
      return body_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (requestId_ != 0) {
        output.writeInt32(1, requestId_);
      }
      if (cmd_ != 0) {
        output.writeInt32(2, cmd_);
      }
      if (errorCode_ != 0) {
        output.writeInt32(3, errorCode_);
      }
      if (playerId_ != 0L) {
        output.writeInt64(4, playerId_);
      }
      if (!body_.isEmpty()) {
        output.writeBytes(5, body_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (requestId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, requestId_);
      }
      if (cmd_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, cmd_);
      }
      if (errorCode_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, errorCode_);
      }
      if (playerId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(4, playerId_);
      }
      if (!body_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(5, body_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.panda.game.proto.PacketPb.Pkg)) {
        return super.equals(obj);
      }
      com.panda.game.proto.PacketPb.Pkg other = (com.panda.game.proto.PacketPb.Pkg) obj;

      if (getRequestId()
          != other.getRequestId()) return false;
      if (getCmd()
          != other.getCmd()) return false;
      if (getErrorCode()
          != other.getErrorCode()) return false;
      if (getPlayerId()
          != other.getPlayerId()) return false;
      if (!getBody()
          .equals(other.getBody())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + REQUESTID_FIELD_NUMBER;
      hash = (53 * hash) + getRequestId();
      hash = (37 * hash) + CMD_FIELD_NUMBER;
      hash = (53 * hash) + getCmd();
      hash = (37 * hash) + ERROR_CODE_FIELD_NUMBER;
      hash = (53 * hash) + getErrorCode();
      hash = (37 * hash) + PLAYERID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getPlayerId());
      hash = (37 * hash) + BODY_FIELD_NUMBER;
      hash = (53 * hash) + getBody().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.panda.game.proto.PacketPb.Pkg parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.panda.game.proto.PacketPb.Pkg parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.panda.game.proto.PacketPb.Pkg prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * <pre>
     *游戏包数据
     * </pre>
     *
     * Protobuf type {@code com.panda.game.proto.Pkg}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.panda.game.proto.Pkg)
        com.panda.game.proto.PacketPb.PkgOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.panda.game.proto.PacketPb.internal_static_com_panda_game_proto_Pkg_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.panda.game.proto.PacketPb.internal_static_com_panda_game_proto_Pkg_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.panda.game.proto.PacketPb.Pkg.class, com.panda.game.proto.PacketPb.Pkg.Builder.class);
      }

      // Construct using com.panda.game.proto.PacketPb.Pkg.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        requestId_ = 0;

        cmd_ = 0;

        errorCode_ = 0;

        playerId_ = 0L;

        body_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.panda.game.proto.PacketPb.internal_static_com_panda_game_proto_Pkg_descriptor;
      }

      @java.lang.Override
      public com.panda.game.proto.PacketPb.Pkg getDefaultInstanceForType() {
        return com.panda.game.proto.PacketPb.Pkg.getDefaultInstance();
      }

      @java.lang.Override
      public com.panda.game.proto.PacketPb.Pkg build() {
        com.panda.game.proto.PacketPb.Pkg result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public com.panda.game.proto.PacketPb.Pkg buildPartial() {
        com.panda.game.proto.PacketPb.Pkg result = new com.panda.game.proto.PacketPb.Pkg(this);
        result.requestId_ = requestId_;
        result.cmd_ = cmd_;
        result.errorCode_ = errorCode_;
        result.playerId_ = playerId_;
        result.body_ = body_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.panda.game.proto.PacketPb.Pkg) {
          return mergeFrom((com.panda.game.proto.PacketPb.Pkg)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.panda.game.proto.PacketPb.Pkg other) {
        if (other == com.panda.game.proto.PacketPb.Pkg.getDefaultInstance()) return this;
        if (other.getRequestId() != 0) {
          setRequestId(other.getRequestId());
        }
        if (other.getCmd() != 0) {
          setCmd(other.getCmd());
        }
        if (other.getErrorCode() != 0) {
          setErrorCode(other.getErrorCode());
        }
        if (other.getPlayerId() != 0L) {
          setPlayerId(other.getPlayerId());
        }
        if (other.getBody() != com.google.protobuf.ByteString.EMPTY) {
          setBody(other.getBody());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.panda.game.proto.PacketPb.Pkg parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.panda.game.proto.PacketPb.Pkg) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int requestId_ ;
      /**
       * <pre>
       * 请求id
       * </pre>
       *
       * <code>int32 requestId = 1;</code>
       * @return The requestId.
       */
      @java.lang.Override
      public int getRequestId() {
        return requestId_;
      }
      /**
       * <pre>
       * 请求id
       * </pre>
       *
       * <code>int32 requestId = 1;</code>
       * @param value The requestId to set.
       * @return This builder for chaining.
       */
      public Builder setRequestId(int value) {
        
        requestId_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 请求id
       * </pre>
       *
       * <code>int32 requestId = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearRequestId() {
        
        requestId_ = 0;
        onChanged();
        return this;
      }

      private int cmd_ ;
      /**
       * <pre>
       *命令号，区分不同命令
       * </pre>
       *
       * <code>int32 cmd = 2;</code>
       * @return The cmd.
       */
      @java.lang.Override
      public int getCmd() {
        return cmd_;
      }
      /**
       * <pre>
       *命令号，区分不同命令
       * </pre>
       *
       * <code>int32 cmd = 2;</code>
       * @param value The cmd to set.
       * @return This builder for chaining.
       */
      public Builder setCmd(int value) {
        
        cmd_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *命令号，区分不同命令
       * </pre>
       *
       * <code>int32 cmd = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearCmd() {
        
        cmd_ = 0;
        onChanged();
        return this;
      }

      private int errorCode_ ;
      /**
       * <pre>
       *错误码
       * </pre>
       *
       * <code>int32 error_code = 3;</code>
       * @return The errorCode.
       */
      @java.lang.Override
      public int getErrorCode() {
        return errorCode_;
      }
      /**
       * <pre>
       *错误码
       * </pre>
       *
       * <code>int32 error_code = 3;</code>
       * @param value The errorCode to set.
       * @return This builder for chaining.
       */
      public Builder setErrorCode(int value) {
        
        errorCode_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *错误码
       * </pre>
       *
       * <code>int32 error_code = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearErrorCode() {
        
        errorCode_ = 0;
        onChanged();
        return this;
      }

      private long playerId_ ;
      /**
       * <pre>
       *玩家唯一ID
       * </pre>
       *
       * <code>int64 playerId = 4;</code>
       * @return The playerId.
       */
      @java.lang.Override
      public long getPlayerId() {
        return playerId_;
      }
      /**
       * <pre>
       *玩家唯一ID
       * </pre>
       *
       * <code>int64 playerId = 4;</code>
       * @param value The playerId to set.
       * @return This builder for chaining.
       */
      public Builder setPlayerId(long value) {
        
        playerId_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *玩家唯一ID
       * </pre>
       *
       * <code>int64 playerId = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearPlayerId() {
        
        playerId_ = 0L;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString body_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <pre>
       *包体的二进制数据
       * </pre>
       *
       * <code>bytes body = 5;</code>
       * @return The body.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString getBody() {
        return body_;
      }
      /**
       * <pre>
       *包体的二进制数据
       * </pre>
       *
       * <code>bytes body = 5;</code>
       * @param value The body to set.
       * @return This builder for chaining.
       */
      public Builder setBody(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        body_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *包体的二进制数据
       * </pre>
       *
       * <code>bytes body = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearBody() {
        
        body_ = getDefaultInstance().getBody();
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:com.panda.game.proto.Pkg)
    }

    // @@protoc_insertion_point(class_scope:com.panda.game.proto.Pkg)
    private static final com.panda.game.proto.PacketPb.Pkg DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.panda.game.proto.PacketPb.Pkg();
    }

    public static com.panda.game.proto.PacketPb.Pkg getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Pkg>
        PARSER = new com.google.protobuf.AbstractParser<Pkg>() {
      @java.lang.Override
      public Pkg parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Pkg(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Pkg> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Pkg> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public com.panda.game.proto.PacketPb.Pkg getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_panda_game_proto_Pkg_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_panda_game_proto_Pkg_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\014packet.proto\022\024com.panda.game.proto\"Y\n\003" +
      "Pkg\022\021\n\trequestId\030\001 \001(\005\022\013\n\003cmd\030\002 \001(\005\022\022\n\ne" +
      "rror_code\030\003 \001(\005\022\020\n\010playerId\030\004 \001(\003\022\014\n\004bod" +
      "y\030\005 \001(\014B \n\024com.panda.game.protoB\010PacketP" +
      "bb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_com_panda_game_proto_Pkg_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_panda_game_proto_Pkg_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_panda_game_proto_Pkg_descriptor,
        new java.lang.String[] { "RequestId", "Cmd", "ErrorCode", "PlayerId", "Body", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
