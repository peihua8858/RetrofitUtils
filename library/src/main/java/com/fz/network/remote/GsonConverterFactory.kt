package com.fz.network.remote

import com.fz.gson.GsonFactory
import com.fz.network.RetrofitClient
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.*
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * A [converter][Converter.Factory] which uses Gson for JSON.
 *
 *
 * Because Gson is so flexible in the types it supports, this converter assumes that it can handle
 * all types. If you are mixing JSON serialization with something else (such as protocol buffers),
 * you must [add this instance][Retrofit.Builder.addConverterFactory]
 * last to allow the other converters a chance to see their types.
 */
class GsonConverterFactory private constructor(
        gson: Gson = GsonFactory.createDefaultBuild().create(),
        mediaType: MediaType = RetrofitClient.MEDIA_TYPE,
) : Converter.Factory() {
    private val gson: Gson?
    private val mediaType: MediaType
    override fun responseBodyConverter(
            type: Type, annotations: Array<Annotation>,
            retrofit: Retrofit,
    ): Converter<ResponseBody, *> {
        if (type === String::class.java) {
            return StringConverter.INSTANCE
        }
        val adapter: TypeAdapter<*>? = gson!!.getAdapter(TypeToken.get(type))
        return GsonResponseBodyConverter(gson, adapter as TypeAdapter<Any>)
    }

    override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit,
    ): Converter<*, RequestBody> {
        val adapter: TypeAdapter<*>? = gson?.getAdapter(TypeToken.get(type))
        return GsonRequestBodyConverter(gson, adapter as TypeAdapter<Any>, mediaType)
    }

    /**
     * http 响应String类型数据处理
     *
     * @author dingpeihua
     * @version 1.0
     * @date 2016/12/24 11:42
     */
    internal class StringConverter : Converter<ResponseBody, String> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): String {
            return value.string()
        }

        companion object {
            val INSTANCE = StringConverter()
        }
    }

    internal class GsonRequestBodyConverter<T>(private val gson: Gson?, private val adapter: TypeAdapter<T>, private val mediaType: MediaType) : Converter<T, RequestBody> {
        @Throws(IOException::class)
        override fun convert(value: T): RequestBody {
            val buffer = Buffer()
            val writer: Writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
            val jsonWriter = gson!!.newJsonWriter(writer)
            adapter.write(jsonWriter, value)
            jsonWriter.close()
            return RequestBody.create(mediaType, buffer.readByteString())
        }

        companion object {
            private val UTF_8 = Charset.forName("UTF-8")
        }
    }

    internal class GsonResponseBodyConverter<T>(private val gson: Gson?, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            return value.use {
                val bis = ByteArrayInputStream(it.bytes())
                val reader: Reader = BufferedReader(InputStreamReader(bis))
                val jsonReader = gson!!.newJsonReader(reader)
                val t = adapter.read(jsonReader)
                reader.close()
                bis.close()
                t
            }
        }
    }

    companion object {
        @JvmStatic
        fun create(mediaType: MediaType): GsonConverterFactory {
            return create(GsonFactory.createDefaultBuild().create(), mediaType)
        }
        /**
         * Create an instance using `gson` for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        /**
         * Create an instance using a default [Gson] instance for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        @JvmOverloads
        @JvmStatic
        fun create(gson: Gson = GsonFactory.createDefaultBuild().create()): GsonConverterFactory {
            return GsonConverterFactory(gson)
        }

        @JvmStatic
        fun create(gson: Gson, mediaType: MediaType): GsonConverterFactory {
            return GsonConverterFactory(gson, mediaType)
        }
    }

    init {
        this.gson = gson
        this.mediaType = mediaType
    }
}