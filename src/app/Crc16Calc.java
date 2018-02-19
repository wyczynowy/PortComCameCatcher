package app;

import java.util.ArrayList;
import java.util.List;

public class Crc16Calc {
	final int P_16 = 0x5b9a;
	static int[] crc_tab16 = new int[256];
	public Crc16Calc() {
		initCrc16Tab();
	}

	public int MakeCrc(List<Integer> data, int size)
	{
		int crc = 0;  //normal crc 16 or 0xffff for modbus crc
		int data16, tmp;
		for (int i = 0; i < size; i++)
		{
			data16 = 0x00ff & data.get(i);
			tmp = crc ^ data16;
			crc = (crc >> 8) ^ crc_tab16[tmp & 0xff];
		}
		return crc;
	}

	void initCrc16Tab()
	{
		int i, j;
		int crc, c;

		for (i = 0; i < 256; i++)
		{

			crc = 0;
			c = i;

			for (j = 0; j < 8; j++)
			{

				if (((crc ^ c) & 0x0001) > 0)
					crc = (crc >> 1) ^ P_16;
				else
					crc = crc >> 1;

				c = c >> 1;
			}
			crc_tab16[i] = crc;
		}
	}
	
	public static void main(String[] args) {
		List<Integer> lista = new ArrayList<Integer>();
		lista.add(01);
		lista.add(02);
		lista.add(01);
		lista.add(0x76);
		Crc16Calc main = new Crc16Calc();
		System.out.println(String.format("0x%04X", main.MakeCrc(lista, 4)));
		System.out.println(String.format("%02X", 97));

	}

}
