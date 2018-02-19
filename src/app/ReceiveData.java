package app;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReceiveData implements Runnable {

	private static ArrayList<Integer> receiveDataBuffer = new ArrayList<Integer>();		// Bufor z danymi odebranymi z portu COM
	private byte[] bytes = new byte[1];										// Zmienna do ktorej zapisywany jest bajt, ktory nadlecial
	private int receivedByte = 0;											// Zmienna przechowujaca wartosc typu int bajtu ktory nadlecial
	private static boolean newFrameFlag = false;									// Flaga informujaca o obecnosci gotowej do odczytu ramki w buforze
	private boolean receivingInProgress = false;							// Flaga informujaca o tym, ze odbieranie ramki jest w trakcie
	private int byteNumber = 0;												// Licznik ilosci bajtow jakie nadlecialy
	private int frameLength = 0;											// Zmienna przechowujaca dane o dlugosci ramki
	private int crcCounted = 0;												// Zmienna przechowujaca obliczone CRC ramki
	private int crcReceived = 0;											// Zmienna przechowujaca otrzymane CRC ramki
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	ReceiveTimer receiveTimer = new ReceiveTimer();
	Crc16Calc crc = new Crc16Calc();										// Obiekt obliczajacy crc
	
	public ReceiveData() {
		executor.execute(receiveTimer);
	}
	
	//*********************************************************************************//
	//
	//   Zwraca informacje o tym czy jest nowa ramka do odczytu w buforze odbiorczym   
	//
	//*********************************************************************************//
	
	public static boolean isNewFrameRedyToRead() {
		return newFrameFlag;
	}
	
	//*********************************************************************************//
	
	//*********************************************************************************//
	//
	//   		Zwraca ramke danych, ktora znajduje sie w buforze odbiorczym  
	//
	//*********************************************************************************//
	
	public static ArrayList<Integer> getNewFrame() {
		newFrameFlag = false;
		return receiveDataBuffer;
	}
	
	//*********************************************************************************//
	
	
	@Override
	public void run() {
		while(true) {
			while(FirstTabContainer.usbPort.bytesAvailable() > 0) {		// Jezeli nadlecial jakis bajt
				FirstTabContainer.usbPort.readBytes(bytes, 1);		// Odczytujemy ten bajt
				receivedByte = Byte.toUnsignedInt(bytes[0]);				// Konwersja na int
				
				if(newFrameFlag) continue;									// Jezeli w buforze znajduje sie ramka czekajaca na przetworzenie to wyjdz
				
				if((receivedByte == 0x55) && (!receivingInProgress || receiveTimer.getTimer() == 0)) {		// Jezeli wlasnie nadlecial bajt rozpoczynajacy ramke i nie jestesmy w trakcie odbioru lub uplynal timeout odioru
						receiveTimer.setTimer(50);		// Ustawiamy timer
						receivingInProgress = true;		// Ustawiamy flege informujaca, ze odior ramki jest w trakcie
						byteNumber = 0;					// Czyscimy licznik ilosci odebranych bajtow
						frameLength = 0;				// Czyscimy dane o dlugosci ramki
						receiveDataBuffer.clear(); 		// Czyscimy bufor odbiorczy
						continue;						// Wyjdz
				}
				
				if(receivedByte == 0x56 && receivingInProgress) {	// Jezeli odebrany bajt to bajt zamykajacy ramke i jestesmy podczas odbioru ramki
					if((frameLength + 5) == (byteNumber)) {		// oraz zgadza sie informacja o dlugosci ramki ZAKODOWANA w dwoch pierwszych bajtach ramki
						crcCounted = crc.MakeCrc(receiveDataBuffer, receiveDataBuffer.size() - 2);	// Obliczamy CRC
						crcReceived = receiveDataBuffer.get(byteNumber - 1);			// Zapisujemy mlodszy bajt CRC
						crcReceived |= (receiveDataBuffer.get(byteNumber - 2) << 8);	// Zapisujemy starszy bajt CRC
						if(crcCounted == crcReceived) {									// Sprawdzamy poprawnosc CRC
							newFrameFlag = true;										// Ustawiamy flage informujaca ze jest poprawna ramka godota do odczytu
							receivingInProgress = false;								// Wyzeruj flage informujaca, ze ramka jest w fazie odbioru
							crcCounted = 0;												// Czyscimy zmienna pomocnicza
							crcReceived = 0;											// Czyscimy zmienna pomocnicza
							continue;													// Wyjdz
						} else {	// Jezeli CRC jest niepoprawne, to jakis blad i reset zmiennych
							receivingInProgress = false;	// Wyzeruj flage informujaca, ze jestesmy w trakcie odbioru
							crcCounted = 0;					// Czysc zmienna pomocnicza
							crcReceived = 0;				// Czysc zmienna pomocnicza
							continue;						// Wyjdz
						}
					}
				}
				
				if(receivingInProgress && (byteNumber == 1)) {	// Jezeli jestesmy w trakcie odbioru i wlasnie nadlecial drugi bajt oprocz bajtu rozpoczynajacego ramke to wyluskujemy informacje o dlugosci ramki
					frameLength = receiveDataBuffer.get(0);
					if((bytes[0] & 0x10) == 0x10)				// Jezeli bit numer 4 jest ustawiony
						frameLength += 256;
					if((bytes[0] & 0x20) == 0x20)				// Jezeli bit numer 5 jest ustawiony
						frameLength += 512;
				}
				
				if(receivingInProgress) {					// Jezeli ramka jest w trakcie odbioru
					if((frameLength + 5) <= byteNumber) {	// Jezeli dlugosc ramki jest wieksza niz zostala zadeklarowana w dwoch pierwszych bajtach po bajcie startowym to jakis blad i resetujemy odbior
						receivingInProgress = false;		// Resetujemy flage informujaca, ze odbior jest w trakcie
						frameLength = 0;					// Czyscimy bledne dane o dlugosci ramki
						byteNumber = 0;						// Zerujemy licznik odebranych bajtow
						continue;							// Wyjdz
					} else {
						receiveDataBuffer.add(receivedByte);	// Zapisujemy odebrany bajt do bufora
						byteNumber++;
					}
				}
			}
	        try {
	            TimeUnit.MILLISECONDS.sleep(1);
	          } catch(InterruptedException e) {
	            System.out.println("InterruptException -> secondTab.SendData");
	            return;
	          }
		}
	}
}
