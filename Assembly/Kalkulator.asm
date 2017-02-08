data segment

	pierwsza	db 30 dup(0),'$'	; pierwsza liczba
	druga		db 30 dup(0),'$'	; druga liczba
	wynik		db 61 dup(0),'$'	; wynik
	pom1		db 30 dup(0),'$'	; tablica pomocnicza
	pom2		db 61 dup(0),'$'	; tablica pomocnicza

	akcja		db ?				; dzialanie do wykonania
	
	pznak		db '+'				; znak pierwszej liczby
	dznak		db '+'				; znak drugiej liczby
	wznak		db '+'				; znak wyniku
	
	pdlugosc	db 0				; dlugosc pierwszej liczby
	ddlugosc	db 0				; dlugosc drugiej liczby
	p1dlugosc	db 0				; dlugosc liczby w tablicy pomocniczej pom1
	
	przen		db 0				; przeniesienie w operacjach pisemnych
	przes		db 0				; przesuniecie liczb przy dodawaniu w operacji mnozenia
	
	pWiad		db 0ah,'Podaj pierwsza liczbe: $'
	dWiad		db 0ah,'Podaj druga liczbe: $'
	
	akcjaWiad	db 0ah,'Podaj znak dzialania: $'
	
	liczbaDozw	db '0','1','2','3','4','5','6','7','8','9'	; dozwolone cyfry
	akcjaDozw	db '+','-','*','/'
	
	dzielZero	db 0ah,'Nie mozesz dzielic przez 0!','$'
	
data ends


code segment
	start:
		
		mov		sp,offset top			; inicjalizacja stosu
		mov		ax,seg top
		mov		ss,ax
		
		;--- WCZYTYWANIE DANYCH ---;
		
		mov		ax,seg pwiad			; wypisanie komunikatu - pierwsza liczba
		mov		ds,ax
		mov		dx,offset pwiad
		mov		ah,9
		int		21h
		
		mov		ax,seg pierwsza			; wczytanie pierwszej liczby
		mov		ds,ax
		mov		bx,offset pierwsza
		mov		ax,seg pznak			; adres znaku liczby trzymamy w es:[si]
		mov		es,ax
		mov		si,offset pznak
		mov		di,offset pdlugosc		; adres dlugosci liczby trzymamy w ds:[di]
		call	pobierzLiczbe
		call	czekajEnter				; !ZMIANA! ;
		cmp		byte ptr es:[si],'-'	; jezeli wczytano liczbe ujemna, nalezy odjac 1 od jej dlugosci
		jne		drugaLiczba
		mov		al,[di]
		dec		al
		mov		byte ptr ds:[di],byte ptr al
		
		drugaLiczba:
		mov		ax,seg dwiad			; wypisanie komunikatu - druga liczba
		mov		ds,ax
		mov		dx,offset dwiad
		mov		ah,9
		int		21h
		
		mov		ax,seg druga			; wczytanie drugiej liczby
		mov		ds,ax
		mov		bx,offset druga
		mov		ax,seg dznak			; adres znaku liczby trzymamy w es:[si]
		mov		es,ax
		mov		si,offset dznak
		mov		di,offset ddlugosc		; adres dlugosci liczby trzymamy w ds:[di]
		call	pobierzLiczbe
		call	czekajEnter				; !ZMIANA! ;
		cmp		byte ptr es:[si],'-'	; jezeli wczytano liczbe ujemna, nalezy odjac 1 od jej dlugosci
		jne		niePodanoPierwszejLiczby
		mov		al,[di]
		dec		al
		mov		byte ptr ds:[di],byte ptr al
		
		niePodanoPierwszejLiczby:
		mov		bx,offset pdlugosc
		mov		al,[bx]
		cmp		al,0					; jezeli nie podano zadnego znaku pierwszej liczby
		jne		przesuwaniePierwszejLiczby
		inc		al
		mov		byte ptr ds:[bx],al
		jmp		niePodanoDrugiejLiczby	; nie ma czego przesuwac, sprawdzamy kolejna liczbe
		
		przesuwaniePierwszejLiczby:
		mov		bx,offset pierwsza		; przesuwanie pierwszej liczby do prawej krawedzi
		mov		si,offset pdlugosc	
		call	wyrownajDoPrawej
				
		niePodanoDrugiejLiczby:
		mov		bx,offset ddlugosc
		mov		al,[bx]
		cmp		al,0					; jezeli nie podano zadnego znaku drugiej liczby
		jne		przesuwanieDrugiejLiczby
		inc		al
		mov		byte ptr ds:[bx],al
		jmp		znakDzialania			; nie ma czego przesuwac, sprawdzamy znak
		
		przesuwanieDrugiejLiczby:
		mov		bx,offset druga			; przesuwanie drugiej liczby do prawej krawedzi
		mov		si,offset ddlugosc	
		call	wyrownajDoPrawej
		
		znakDzialania:
		mov		ax,seg akcjaWiad		; wypisanie komunikatu - symbol dzialania
		mov		ds,ax
		mov		dx,offset akcjaWiad
		mov		ah,9
		int		21h
		
		mov		ax,seg akcja			; wczytanie symbolu dzialania
		mov		ds,ax
		mov		bx,offset akcja
		call	pobierzAkcje
		
		
		;--- ZNAK DZIALANIA ---;
		
		mov		bx,offset pznak
		mov		al,[bx]
		mov		bx,offset dznak
		mov		ah,[bx]
		mov		bx,offset akcja
		mov		dh,[bx]
		
		cmp		dh,'/'
		je		wybranoDzielenie
		
		cmp		dh,'*'
		je		wybranoMnozenie
		
		cmp		dh,'-'
		je		wybranoOdejmowanie
		
		jmp		wybranoDodawanie
		
		
		wybranoDzielenie:						;--- OBSLUGA DZIELENIA ---;
			mov		bx,offset druga
			mov		cx,30
			
			sprawdzZero:
				mov		dl,[bx]
				cmp		dl,0
				jne		poprawnyDzielnik
				inc		bx
				loop	sprawdzZero
				
				mov		dx,offset dzielZero
				mov		ah,9
				int		21h
				jmp		koniecProgramu
			
			poprawnyDzielnik:
			cmp		al,'-'
			jne		drugiZnakDziel
			call	zmienZnak
			
			drugiZnakDziel:
				cmp		ah,'-'
				jne		startDzielenie
				call	zmienZnak
				
			startDzielenie:
				call	podzielLiczby
				jmp		wynikDzialania
			
			
		wybranoMnozenie:						;--- OBSLUGA MNOZENIA ---;
			cmp		al,'-'
			jne		drugiZnakMnoz
			call	zmienZnak
			
			drugiZnakMnoz:
				cmp		ah,'-'
				jne		startMnozenie
				call	zmienZnak
				
			startMnozenie:
				call	pomnozLiczby
				jmp		wynikDzialania
		
		
		wybranoOdejmowanie:						;--- OBSLUGA ODEJMOWANIA ---;
			cmp		al,'+'
			jne		pierwszaUjemnaOdejm
			
			cmp		ah,'+'
			jne		plusMinusOdejm
			call	pierwszaMinusDruga			; jesli obie liczby sa dodatnie
			jmp		wynikDzialania
			
			plusMinusOdejm:
				call	pierwszaPlusDruga		; jesli pierwsza liczba jest dodatnia, druga ujemna
				jmp		wynikDzialania
			
			pierwszaUjemnaOdejm:				; jesli pierwsza liczba jest ujemna
				cmp		ah,'+'
				jne		obieUjemneOdejm
				call	zmienZnak
				call	PierwszaPlusDruga		; jesli pierwsza liczba jest ujemna, druga dodatnia
				jmp		wynikDzialania
				
				obieUjemneOdejm:				; jesli obie liczby sa ujemna
					call	drugaMinusPierwsza
					jmp		wynikDzialania
		
		
		wybranoDodawanie:						;--- OBSLUGA DODAWANIA ---;
			cmp		al,'+'
			jne		pierwszaUjemnaDod
			
			cmp		ah,'+'
			jne		plusMinusDod
			call	pierwszaPlusDruga			; jesli obie liczby sa dodatnie
			jmp		wynikDzialania
			
			plusMinusDod:
				call	pierwszaMinusDruga		; jesli pierwsza liczba jest dodatnia, druga ujemna
				jmp		wynikDzialania
			
			pierwszaUjemnaDod:					; jesli pierwsza liczba jest ujemna
				cmp		ah,'+'
				jne		obieUjemneDod
				call	drugaMinusPierwsza		; jesli pierwsza liczba jest ujemna, druga dodatnia
				jmp		wynikDzialania
				
				obieUjemneDod:					; jesli obie liczby sa ujemna
					call	zmienZnak
					call	pierwszaPlusDruga
					jmp		wynikDzialania
		
		
		;--- WYPISYWANIE WYNIKU ---;
		
		wynikDzialania:
		mov		dl,0ah						; nowa linia
		mov		ah,2
		int		21h
		
		mov		bx,offset wznak				; sprawdzenie znaku wyniku
		mov		al,[bx]
		cmp		al,'-'
		jne		wypiszBezZnaku
		
		mov		dl,al						; wypisanie znaku minus
		mov		ah,2
		int		21h
		
		wypiszBezZnaku:
			mov		bx,offset wynik
			mov		cx,61
			
			zeraNieznaczace:				; ignoruje zera nieznaczace
				mov		al,[bx]
				cmp		al,0
				jne		nieZero
				inc		bx
				loop	zeraNieznaczace
			
			mov		dl,'0'					; jesli wynik sklada sie z samych zer
			mov		ah,2
			int		21h
			jmp		koniecProgramu
			
			nieZero:						; jesli wynik jest rozny od 0
				wypiszWynik:
					mov		al,[bx]
					cmp		al,'#'
					je		koniecProgramu
					
					add		al,30h
					mov		dl,al
					mov		ah,2
					int		21h
					inc		bx
					loop	wypiszWynik
		
		
		;--- ZAKONCZENIE PROGRAMU ---;
		
		koniecProgramu:
			mov		ah,10h				; czeka na znak
			int		16h
			mov		ah,4ch				; koniec programu
			int		21h		
		
		
		
;*** OBSŁUGA WCZYTYWANIA DANYCH ***;
	
		pobierzLiczbe proc
			xor		cx,cx
			mov		cx,30					; maksymalna ilosc znakow do wczytania
			nowaCyfra:
				push	di					; wrzucamy na stos adres dlugosci liczby (wewnatrz programu uzywamy pomocniczo rejestru di)
				push	cx
				call	wczytajZnak
				cmp		al,0dh				; jesli wcisnieto ENTER
				je		wcisnietoEnter
				inc		bx					; zwiekszamy bx - kolejne miejsce w tablicy przechowujacej liczbe
				pop		cx
				pop		di					; sciagamy ze stosu adres dlugosci liczby i zwiekszamy wartosc zmiennej o 1
				mov		al,[di]
				inc		al
				mov		byte ptr ds:[di],byte ptr al
				loop	nowaCyfra	
				
			ret								; zakonczenie procedury po przejsciu petli
			
			wcisnietoEnter:					; zakonczenie procedury jesli wcisnieto enter
				pop		cx
				pop		di
				ret
		pobierzLiczbe endp	
			
			
		wczytajZnak proc
			wczytaj:									; pobiera znak
				mov		ah,10h
				int		16h
			
			cmp		cx,30								; pierwszy wczytywany znak moze byc minusem
			jne		nieMinus	
			cmp		byte ptr al,byte ptr '-'
			jne		nieMinus			
			mov		byte ptr es:[si],byte ptr al		; zapisanie znaku liczby pod es:[si]
			mov		dl,al								; wypisanie znaku na ekran
			mov		ah,2
			int		21h
			pop		ax									; sciagamy adres powrotu ze stosu do ax
			pop		cx									; sciagamy licznik petli do cx
			inc		cx									; zwiekszamy cx, aby wczytac maksymalnie 30 znakow (nie 29)
			dec		bx									; cofamy sie o jeden bajt w tablicy przechowujacej cyfry, aby poprawnie wpisac
			push	cx									; wrzucamy na stos licznik petli i adres powrotu
			push	ax
			ret
			
			nieMinus:
			push	ds									; odkladamy na stos adres tablicy z liczba	
			push	bx
			push	ax									; odkladamy na stos ASCII wczytanego znaku	
			
			mov		ax,seg liczbaDozw					; adres tablicy przechowujacej dozwolone znaki
			mov		ds,ax
			mov		bx,offset liczbaDozw
			mov		cx,10								; ilosc powtorzen - tablica przechowuje 10 cyfr
			pop		ax									; zdejmujemy ze stosu ASCII wczytanego znaku
			
			sprawdzCyfre:								; sprawdza, czy wprowadzono poprawny znak
				cmp		byte ptr ds:[bx],byte ptr al	; porownaj ASCII wprowadzonego znaku z dozwolonymi znakami
				je		zapiszCyfre
				inc		bx								; jesli nie znaleziono - kolejny znak
				loop	sprawdzCyfre
				
			cmp		al,0dh								; jesli wcisnieto ENTER
			je		zakonczZnak

			pop		bx									; zdejmujemy rejestry, ktore pozostaly na stosie
			pop		ds
			pop		di									; sciagamy ze stosu adres powrotu
			pop		cx									; sciagamy ze stosu licznik glownej petli (cyfry liczby)
			push	cx									; zapisujemy w cx licznik glownej petli (aby moc wczytac ponownie znak '-' na poczatku)
			push	di									; wrzucamy na stos adres powrotu
			jmp		wczytaj								; jesli wprowadzono niepoprawny znak - jeszcze raz
			
			zapiszCyfre:
				mov		dl,al
				push	ax								; wrzucamy ASCII wczytanego znaku na stos
				mov		ah,2							; wypisanie wczytanego znaku
				int		21h
				pop		ax								; zdejmujemy ze stosu ASCII wczytanego znaku
				pop		bx								; zdejmujemy ze stosu adres tablicy z liczba
				pop		ds
				sub		al,30h							; zamiana ASCII na rzeczywista cyfre
				mov		byte ptr ds:[bx],byte ptr al	; wpisujemy cyfre do tablicy			
				ret
			
			zakonczZnak:								; powrot do procedury pobierzLiczbe
				pop		bx
				pop		ds
				ret	
		wczytajZnak endp
		
		
		pobierzAkcje proc
			wczytajSymbol:
				mov		ah,10h
				int		16h
			
			push	ds					; wrzucamy na stos adres zmiennej pamietajacej znak i ASCII wczytanego znaku
			push	bx
			push	ax
			mov		cx,4				; cztery mozliwe znaki dzialan
			mov		ax,seg akcjaDozw
			mov		ds,ax
			mov		bx,offset akcjaDozw
			pop		ax
			
			sprawdzSymbol:
				cmp		byte ptr ds:[bx],byte ptr al
				je		zapiszSymbol
				inc		bx
				loop	sprawdzSymbol
			pop bx
			pop ds
			jmp wczytajSymbol
				
			zapiszSymbol:
				mov		dl,al
				push	ax
				mov		ah,2
				int		21h
				pop		ax
				pop		bx
				pop		ds
				mov		byte ptr ds:[bx],byte ptr al
			ret
		pobierzAkcje endp
		
		
		wyrownajDoPrawej proc		
			xor		cx,cx					; wpisujemy do cx licznik powtorzen - dlugosc liczby
			mov		cl,[si]
			mov		si,bx					; ds:[si] wskazuje na ostatni wolny element tablicy z liczba
			add		si,29
			add		bx,cx					; ds:[bx] wskazuje na ostatnia nieprzetworzona cyfre liczby
			dec		bx
			cmp		si,bx
			je		zakonczWyrownywanie
			
			przesunCyfre:
				mov		dl,[bx]				; przenosimy pierwsza nieprzetworzona cyfre na ostatnie wolne miejsce
				mov		[si],dl
				mov		byte ptr ds:[bx],0	; w miejsce przeniesionej cyfry wpisujemy 0
				dec		bx					; przesuwamy sie na kolejne nieprzetworzone/wolne miejsca
				dec		si				
				loop	przesunCyfre
				
			zakonczWyrownywanie:
				ret
		wyrownajDoPrawej endp


;*** OBSLUGA ZNAKU DZIALANIA ***;

		pierwszaPlusDruga proc
			mov		bx,offset pierwsza
			mov		si,offset druga
			mov		di,offset wynik
			add		bx,29
			add		si,29
			add		di,60
			mov		cx,30
			call	dodajLiczby
			ret
		pierwszaPlusDruga endp

		pierwszaMinusDruga proc
			mov		bx,offset pierwsza
			mov		si,offset druga
			call	porownajLiczby
			
			cmp		ax,2
			jne		wykonajBezZmianPD
			
			call	zmienZnak
			mov		bx,offset druga
			mov		si,offset pierwsza
			jmp		wspolneDlaObuPD
			
			wykonajBezZmianPD:
				mov		bx,offset pierwsza
				mov		si,offset druga
			
			wspolneDlaObuPD:
				mov		di,offset wynik
				add		bx,29
				add		si,29
				add		di,60
				call	odejmijLiczby
				ret
		pierwszaMinusDruga endp
		
		drugaMinusPierwsza proc
			mov		bx,offset druga
			mov		si,offset pierwsza
			call	porownajLiczby
			
			cmp		ax,2
			jne		wykonajBezZmianDP
			
			call	zmienZnak
			mov		bx,offset pierwsza
			mov		si,offset druga
			jmp		wspolneDlaObuDP
			
			wykonajBezZmianDP:
				mov		bx,offset druga
				mov		si,offset pierwsza
			
			wspolneDlaObuDP:
				mov		di,offset wynik
				add		bx,29
				add		si,29
				add		di,60
				call	odejmijLiczby
				ret
		drugaMinusPierwsza endp

		
;*** DODAWANIE DWOCH LICZB ***;
		
		dodajLiczby proc					; pierwszy skladnik w ds:[bx], drugi skladnik w ds:[si]. wynik w ds:[di]
			dodajJedenZnak:
				push	cx
				xor		ax,ax				; jeden skladnik wrzucamy do al, drugi do dl
				xor		dx,dx
				xor		cx,cx
				mov		al,[bx]		
				mov		dl,[si]
				add		ax,dx				; procedura dodawania
				
				push	bx					; wrzucamy adres pierwszego skladnika na stos - bedziemy korzystac roboczo z rejestru bx
				mov		bx,offset przen		; dodanie przeniesienia z poprzedniej operacji
				mov		cl,[bx]
				add		ax,cx				; koncowy wynik pojedynczego dodawania
				mov		dl,10				; dzielenie modulo
				div		dl
				mov		byte ptr ds:[di],ah	; do wyniku wrzucamy ostatnia cyfre (modulo 10) - reszta z dzielenia zapisana w ah, iloraz - w al
				mov		byte ptr ds:[bx],al	; ustawiamy nowe przeniesienie na kolejna pare znakow
				pop		bx					; sciagamy ze stosu adres pierwszego skladnika ponownie do bx

				dec		bx					; przesuwamy sie o jedno miejsce w lewo na kolejne nieprzetworzone cyfry
				dec		si
				dec		di
				pop		cx
				loop	dodajJedenZnak
				
			push	bx						; jesli pozostalo przeniesienie na 31 cyfre
			mov		bx,offset przen
			mov		al,[bx]
			cmp		al,0
			je		zakonczDodawanie
			mov		byte ptr ds:[di],al
			dec		di
			
			zakonczDodawanie:
				pop		bx
				ret
		dodajLiczby endp
		
		
;*** ODEJMOWANIE DWOCH LICZB ***;

		odejmijLiczby proc					; odjemna (wieksza z liczb) w ds:[bx], odjemnik (mniejsza z liczb) w ds:[si], wynik w ds:[di]
			mov		cx,30
			
			odejmijJedenZnak:
				push	cx
				xor		ax,ax				; odjemna w al, odjemnik w dl
				xor		dx,dx
				xor		cx,cx
				mov		al,[bx]
				mov		dl,[si]
				
				cmp		al,dl
				jae		wykonajOdejmowanie
				call	wykonajPrzeniesienie
				add		al,10
					
				wykonajOdejmowanie:
					sub		al,dl
					mov		byte ptr ds:[di], al
				
				dec		bx
				dec		si
				dec		di
				pop		cx
				loop odejmijJedenZnak
				
			ret
		odejmijLiczby endp
		
		wykonajPrzeniesienie proc
		
			dec		bx
			mov		cl,[bx]
			cmp		cl,0
			jne		zapiszPrzeniesienie
			
			mov		cl,9
			mov		byte ptr ds:[bx],cl
			push	bx
			call	wykonajPrzeniesienie
			pop		bx
			inc		bx
			ret
			
			zapiszPrzeniesienie:
				dec		cl
				mov		byte ptr ds:[bx],cl
				inc		bx
				ret
		wykonajPrzeniesienie endp
		
		
;*** MNOZENIE DWOCH LICZB ***;

		pomnozLiczby proc
			xor		cx,cx
			xor		dx,dx
			mov		bx,offset ddlugosc
			mov		cl,[bx]					; licznik powtorzen petli mnozenia
			
			mov		di,offset druga			; ustawiamy sie na koniec drugiego czynnika
			add		di,29
			
			pomnoz:
				push	cx
				mov		dl,[di]				; cyfre, przez ktora bedziemy mnozyc przechowujemy w rejestrze dl
				push	di					; wrzucamy adres aktualnie przetwarzanej cyfry na stos - wewnatrz procedury wykorzystujemy rejestr di
				call	pomnozPrzezCyfre	; wywolanie procedury mnozenia pierwszej liczby przez cyfre drugiej liczby wraz z dodaniem do wyniku
				pop		di
				mov		bx,offset przes		; przesuniecie wyniku pojedynczego mnozenia o jedna pozycje w lewo
				mov		al,[bx]
				inc		al
				mov		byte ptr ds:[bx],al
				dec		di					; kolejny nieprzetworzony znak
				pop		cx
				loop pomnoz
			
			ret
		pomnozLiczby endp
		
		pomnozPrzezCyfre proc		
			mov		bx,offset przen			; zerujemy przeniesienie poczatkowe (moglo pozostac z mnozenia przez poprzednia cyfre)
			mov		byte ptr ds:[bx],0
			
			mov		bx,offset pom2			; zerujemy tablice pomocnicza
			mov		cx,61
			
			zerujTablice:
				mov		byte ptr ds:[bx],0
				inc		bx
				loop	zerujTablice
			
			mov		bx,offset przes			; ustawienie miejsca, od ktorego rozpoczyna sie wpisywanie wyniku mnozenia
			xor		ax,ax
			mov		al,[bx]					; zapisujemy w al przesuniecie w mnozeniu
			mov		bx,offset pom2
			add		bx,60
			sub		bx,ax					; ustawiamy ostateczny adres komorki, od ktorej rozpoczniemy wypisywanie
			
			mov		si,offset pdlugosc		; ustawiamy licznik petli - ilosc cyfr pierwszej liczby
			xor		cx,cx
			mov		cl,[si]
			
			mov		si,offset pierwsza		; zapisujemy adres pierwszej cyfry do przetwarzania
			add		si,29
			
			wykonajMnozenie:
				push	cx
				xor		ax,ax				; mnozenie dwoch cyfr
				mov		al,[si]
				mul		dl					; mnozenie al przez dl - wynik w ax
				
				push	si					; wrzucamy adres aktualnie przetwarzanej cyfry na stos - korzystamy z rejestru si
				mov		si,offset przen		; dodanie przeniesienia z poprzedniej operacji
				xor		cx,cx
				mov		cl,[si]
				add		ax,cx
				
				mov		cl,10				; dzielenie modulo - przeniesienie w al (div), wynik operacji w ah (mod)
				div		cl				
				mov		byte ptr ds:[si],al	; zapisujemy przeniesienie
				mov		byte ptr ds:[bx],ah	; zapisujemy wynik
				pop		si
				
				dec		si					; kolejna cyfra do przetworzenia
				dec		bx					; kolejne wolne miejsce w tablicy pomocniczej
				pop		cx
				loop	wykonajMnozenie
			
			cmp		al,0					; sprawdzamy przeniesienie koncowe
			je		bezPrzeniesienia
			mov		si,offset przen			; jesli przeniesienie jest niezerowe - dopisujemy je na poczatku utworzonej liczby
			mov		al,[si]
			mov		byte ptr ds:[bx],al
			dec		bx
			
			bezPrzeniesienia:
			mov		bx,offset przen			; zerujemy ewentualne przeniesienie (bedziemy korzystac z tej zmiennej w operacji dodawania)
			mov		byte ptr ds:[bx],0
			
			mov		bx,offset pom2			; pierwszy skladnik sumy
			add		bx,60
			mov		si,offset wynik			; drugi skladnik sumy
			add		si,60
			
			mov		di,offset wynik			; wynik dodawania
			add		di,60
			
			mov		cx,61					; procedura dodawania posredniego w operacji mnozenia
			call	dodajLiczby	
			ret
		pomnozPrzezCyfre endp

		
;*** DZIELENIE DWOCH LICZB ***;

		podzielLiczby proc
			mov		bx,offset pierwsza
			mov		si,offset druga			
			call	porownajLiczby
			
			cmp		ax,0
			je		rowneLiczby						; jesli obie liczby sa rowne, to wynikiem dzielenia jest 1
			cmp		ax,2
			je		zakonczDzielenie				; jesli dzielnik jest wiekszy od dzielnej, to wynik dzielenia calkowitoliczbowego jest rowny 0
			jmp		wykonajDzielenie
			
			rowneLiczby:
			mov		bx,offset wynik
			add		bx,60
			mov		byte ptr ds:[bx],1
			jmp		zakonczDzielenie
			
			wykonajDzielenie:
			xor		cx,cx
			mov		bx,offset pdlugosc				; zapisujemy w cl licznik powtorzen petli - ilosc cyfr dzielnej
			mov		cl,[bx]
			
			mov		di,offset pierwsza				; zapisujemy w di adres pierwszej niezerowej cyfry dzielnej
			add		di,30
			sub		di,cx
			
			mov		si,offset wynik					; zapisujemy w si adres ostatniej cyfry dzielnika	
			
			kolejnaCyfra:
				push	cx
		
				call	przesunLewo					; dopisuje kolejna cyfre pierwszej liczby do aktualnej wartosci dzielnej
				xor		dx,dx						; w dx przechowujemy wynik dzielenia
				mov		cx,10						; maksymalny wynik pojedynczego dzielenia	
				
				dziel:
					push	cx
					push	si						; wrzucamy adres miejsca w wyniku dla danej liczby na stos - korzystamy z rejestru si
					push	di						; wrzucamy adres aktualnie przetwarzanej cyfry na stos - odejmowanie zwraca wynik w ds:[di]
					push	dx						; wrzucamy aktualny wynik na stos - odejmowanie korzysta z rejestru dx
					
					mov		si,offset druga			; zapisujemy w si adres dzielnika
					mov		bx,offset pom1			; zapisujemy w bx offset tablicy przechowujacej aktualna dzielna
					call	porownajliczby			; porownujemy, czy od ds:[bx] (dzielna) mozna odjac ds:[si] (dzielnik)
					
					cmp		ax,2
					je		zapiszWynikDzielenia	; jesli dzielna jest zbyt mala - zapisujemy wynik dzielenia
					
					mov		bx,offset pom1			; odjemna w ds:[bx]
					mov		si,offset druga			; odjemnik w ds:[si]
					mov		di,offset pom1			; wynik w ds:[di]
					add		si,29					; przesuwamy sie na ostatnie miejsca w tablicy
					add		bx,29
					add		di,29
					call	odejmijLiczby			; odejmowanie
					
					pop		dx						; sciagamy ze stosu wrzucone na poczatku petli rejestry
					inc		dl						; przechodzimy do kolejnej cyfry pierwszej liczby
					pop		di
					pop		si
					pop		cx
					loop dziel
						
				zapiszWynikDzielenia:
					pop		dx						; sciagamy ze stosu pozostale rejestry
					pop		di
					pop		si
					pop		cx
					mov		byte ptr ds:[si],dl		; zapisujemy wynik pojedynczego dzielenia
				
				inc		di							; kolejna nieprzetworzona cyfra dzielnej
				inc		si							; kolejna cyfra wyniku
				pop		cx
				loop	kolejnaCyfra
			
			zakonczDzielenie:
			mov		byte ptr ds:[si],'#'			; wrzucamy '#' na koniec tablicy wynikowej, aby wiedziec kiedy zakonczyc wypisywanie
			ret		
		podzielLiczby endp

		przesunLewo proc							; procedura dopisujaca do aktualnej dzielnej kolejna cyfre pierwszej liczby
			mov		bx,offset pom1
			mov		cx,29
			
			przesunPozycje:
				inc		bx							; cyfre z pozycji i+1 - wszej przepisujemy na pozycje i-ta
				mov		al,[bx]						; pobieramy cyfre z pozycji i+1 - wszej
				dec		bx
				mov		byte ptr ds:[bx],al			; zapisujemy cyfre na pozycji i-tej
				
				inc		bx							; kolejna nieprzetworzona cyfra
				loop	przesunPozycje
			
			mov		al,[di]							; na koniec dopisujemy kolejna cyfre pierwszej liczby
			mov		byte ptr ds:[bx],al
			ret
		przesunLewo endp
		
		
;*** INNE PROCEDURY ***;	

		czekajEnter proc					; !ZMIANA! ;
			sprawdzZnak:					; DODANA METODA ;
				mov		ah,10h
				int		16h
				cmp		al,0dh
				je		podanoEnter
				jmp		sprawdzZnak
			
			podanoEnter:
				ret
		czekajEnter endp
		
		porownajLiczby proc					; porownuje liczby ds:[bx],ds[si]; wynik zwracany w ax: 0 - rowne, 1 - pierwsza wieksza, 2 - druga wieksza
			mov		cx,30
			
			porownajCyfre:
				mov		al,[bx]
				mov		ah,[si]
				cmp		al,ah
				ja		pierwszaWieksza		; jesli na tej samej pozycji napotkano rozne cyfry
				jb		drugaWieksza
				inc		bx
				inc		si
				loop	porownajCyfre
			
			obieRowne:
				xor		ax,ax
				mov		ax,0
				ret
			
			pierwszaWieksza:
				xor		ax,ax
				mov		ax,1
				ret
			
			drugaWieksza:
				xor		ax,ax
				mov		ax,2
				ret				
		porownajLiczby endp
		
		zmienZnak proc
			push	bx
			push	ax
			mov		bx,offset wznak
			mov		al,[bx]
			
			cmp		al,'+'
			je		naMinus
			
			mov		byte ptr ds:[bx],'+'
			jmp		zakonczZmiane
			
			naMinus:
				mov		byte ptr ds:[bx],'-'
			
			zakonczZmiane:
				pop		ax
				pop		bx
				ret
		zmienZnak endp
		
code ends


;*** DEKLARACJA STOSU ***;

myStack segment stack
		dw 255 dup(?)					; !ZMIANA! ;
	top	dw ?							; ZAMIANA TEJ LINIJKI Z POWYZSZA ;
myStack	 ends

	end start