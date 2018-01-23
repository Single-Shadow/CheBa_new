package com.aou.cheba.utils;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 序列化工具
 * 
 * @author 李daiaosi
 */
public class SerializeUtils
{
	/**
	 * 序列化
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public synchronized static byte[] serialize(Object object) throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		byte[] data = baos.toByteArray();
		oos.close();
		baos.close();
		return data;
	}

	/**
	 * 反序列化
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public synchronized static <T> T unserialize(Class<T> clazz, byte[] bytes) throws Exception
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		T o = (T) ois.readObject();
		ois.close();
		bais.close();
		return o;
	}

	/**
	 * 反序列化
	 * 
	 * @param
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public synchronized static <T> T unserialize(Class<T> clazz, InputStream stream) throws Exception
	{
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		T o = (T) ois.readObject();
		ois.close();
		bais.close();
		return o;
	}

	/**
	 * 对象转JSON
	 * 
	 * @param object
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String object2Json(Object object) throws Exception
	{
		String json = null;
		ObjectMapper mapper = new ObjectMapper();
		json = mapper.writeValueAsString(object);
		return json;
	}

	/**
	 * JSON转对象
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static <T> T json2Object(String json, Class<T> clazz) throws Exception
	{
		T rspObj = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		rspObj = mapper.readValue(json, clazz);
		return rspObj;
	}

	/**
	 * JSONTOObject
	 * 
	 * @param data
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> T json2Object(byte[] data, Class<T> clazz) throws Exception
	{
		T rspObj = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		rspObj = mapper.readValue(data, clazz);
		return rspObj;
	}

	/**
	 * JSONTOObject
	 * 
	 * @param data
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> T json2Object(InputStream data, Class<T> clazz) throws Exception
	{
		T rspObj = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		rspObj = mapper.readValue(data, clazz);
		return rspObj;
	}

	/**
	 * 反序列化
	 * 
	 * @param <T>
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> unserializeList(Class<T> clazz, List<byte[]> bytes) throws Exception
	{
		ArrayList<T> datas = new ArrayList<>();
		Iterator<byte[]> idatas = bytes.iterator();
		while (idatas.hasNext())
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(idatas.next());
			ObjectInputStream ois = new ObjectInputStream(bais);
			datas.add((T) ois.readObject());
			ois.close();
			bais.close();
		}
		return datas;
	}
}