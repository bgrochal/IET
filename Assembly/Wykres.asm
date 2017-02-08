data segment

	infoWykres	db "Fragment jednostkowy na osi odpowiada 10 pikselom.",13,10,"Wcisnij dowolny klawisz, aby narysowac wykres! $"
	zaDuze		db "Podany argument jest zbyt duzy! Akceptowalne argumenty z przedzialu: [-10; 10]. $"
	bledneArg	db "Wprowadzono bledne dane! $"
	bezArg 		db "Nie podano argumentow! $" 
	argumenty	db 32 dup('$')
	
data ends

code segment
start:
	
	;--- INICJALIZACJA ---;
	
	mov		ax, seg top
	mov		ss, ax
	mov		sp, offset top

	
	;--- LINIA POLECEŃ ---;
	
	xor		bx,bx
	mov		bl,byte ptr ds:[80h]		; DS - wskazuje na segment programu
	cmp 	bl, 0						; Pusty bufor - brak argumentów wywołania
	jne 	podanoArgumenty
	jmp		bezArgumentow				; Jeśli argumenty nie zostały podane

podanoArgumenty:
	mov		ax,seg data
	mov		es,ax
	mov		si,offset argumenty 		; Argumenty w es:[si]

	xor 	dx,dx
	xor		bp,bp
	xor 	ax,ax
	mov		cx,bx						; Licznik pętli to ilość znaków w linii poleceń
	
	
	;--- CZYTANIE ARGUMENTÓW ---;
	
czytajZnak:	
	push	cx
	mov		al,byte ptr ds:[82h + bp]	
	mov		byte ptr es:[si],al			; Przepisujemy znak z bufora do es:[si] (81h - spacja, 82h - początek bufora)
	call 	parsujZnak
	pop		cx
	loop 	czytajZnak
	
przypiszZmienne:						; Przypisujemy wartości (ze zmiennych wczytanych) zmiennym, na których będziemy operować w czasie rysowania
	xor 	ax,ax
	mov  	al,byte ptr cs:[a]  
	cmp  	al,0
	jge  	wiekszeA
	mov  	cx,0						; Zawartość al jest mniejsza od 0
	sub  	cl,al						; Cl - AL jest większe od 0 wobec powyższego
	mov  	ax,cx
	mov  	cx,-1	
	imul  	cx							; AX = -AX
wiekszeA:
	mov 	word ptr cs:[osta],ax		; Możemy dokonać przypisania, bo al jest nieujemne

	xor 	ax,ax						; Analogicznie dla współczynnika b
	mov 	al,byte ptr cs:[b]  
	cmp 	al,0
	jge 	wiekszeB
	mov 	cx,0
	sub 	cl,al
	mov 	ax,cx
	mov 	cx,-1
	imul 	cx
wiekszeB:
	mov 	word ptr cs:[ostb],ax

	xor 	ax,ax						; Analogicznie dla współczynnika c
	mov 	al,byte ptr cs:[wc]
	cmp 	al,0
	jge 	wiekszeC
	mov 	cx,0
	sub 	cl,al
	mov 	ax,cx
	mov 	cx,-1
	imul 	cx
wiekszeC:
	mov 	word ptr cs:[ostc],ax

	
	;--- WALIDACJA ARGUMENTÓW ---;
	
	cmp 	byte ptr cs:[a],100
	jg		niepoprawnyArgument
	cmp 	byte ptr cs:[a],-100
	jl		niepoprawnyArgument
	cmp 	byte ptr cs:[b],100
	jg		niepoprawnyArgument
	cmp 	byte ptr cs:[b],-100
	jl		niepoprawnyArgument
	cmp 	byte ptr cs:[wc],100
	jg		niepoprawnyArgument
	cmp 	byte ptr cs:[wc],-100
	jl		niepoprawnyArgument

	jmp		rozpocznijGrafike
	
	niepoprawnyArgument:
		mov		ax,seg data
		mov		ds,ax
		mov		dx,offset zaDuze
		
		mov		ah,9
		int		21h
		
		mov		ah,4ch
		int		21h
	
	
	;--- TRYB GRAFICZNY ---;

rozpocznijGrafike:	
	mov		ax, seg infoWykres
	mov		ds,ax
	mov		dx,offset infoWykres
	mov		ah,9
	int		21h
	
	xor		ax,ax						; Czekaj na klawisz
	int		16h

	mov		al,13h  					; Tryb graficzny: 320x200 px, 256 kolorów
	mov		ah,0
	int		10h

	
	;--- RYSOWANIE OSI OX ---;
	
	mov		word ptr cs:[x],0 
	mov		word ptr cs:[y],100
	mov		byte ptr cs:[kolor],90

	mov		cx,320
OX:	
	push	cx
	call	zapalPunkt
	
	push	ax
	mov		ax,cx
	mov		cl,10
	div		cl
	cmp		ah,0
	jne		nastepnyX
	dec		word ptr cs:[y]
	call	zapalPunkt
	dec		word ptr cs:[y]
	call	zapalPunkt
	inc		word ptr cs:[y]
	inc		word ptr cs:[y]
	
nastepnyX:
	pop		ax
	inc		word ptr cs:[x]
	pop		cx
	loop	OX
	
	
	;--- RYSOWANIE OSI OY ---;
	
	mov		word ptr cs:[x],160
	mov		word ptr cs:[y],0
	mov		byte ptr cs:[kolor],90
	mov		cx,200

OY:	push	cx
	call	zapalPunkt
	
	push	ax
	mov		ax,cx
	mov		cl,10
	div		cl
	cmp		ah,0
	jne		nastepnyY
	inc		word ptr cs:[x]
	call	zapalPunkt
	inc		word ptr cs:[x]
	call	zapalPunkt
	dec		word ptr cs:[x]
	dec		word ptr cs:[x]
	
nastepnyY:
	pop		ax
	inc		word ptr cs:[y]
	pop		cx
	loop	OY
	

	;--- RYSOWANIE WYKRESU ---;
	
	mov		word ptr cs:[x],0 
	mov		word ptr cs:[y],-1
	mov		byte ptr cs:[kolor],112
	mov		cx,320
	
rysuj:	
	push	cx
	call 	liczWartosc
	
	add		ax,100   					; Ustawienie na środku ekranu względem OY
	mov		bx,ax
	
	cmp		ax,0 						; Sprawdzamy, czy poza ekranem
	jl 		nastepnyArgument	
	cmp 	ax,200
	jge 	nastepnyArgument
	
	mov 	ax,200						; Odwracanie wykresu
	sub 	ax,bx
	mov 	bx,ax						; Wartość współrzędnej y w ax i bx
	
	cmp 	word ptr cs:[y],-1			; Jeśli y = -1, to uprzednio żaden punkt nie znalazł się poza ekranem
	je 		bezUciaglania
	
	sub 	ax,word ptr cs:[y]			; Różnica pomiędzy aktualną i poprzednią wartością współrzędnej na osi OY zapisana w ax
	cmp		ax,0						; Jeśli różnica ta jest równa -1, 0 lub 1 - pomiń
	je 		bezUciaglania
	cmp 	ax,1
	je 		bezUciaglania
	cmp 	ax,-1
	je 		bezUciaglania

	call 	uciaglanie					; Wywołanie uciąglania wykresu
	jmp 	nastepnyArgument

	
	bezUciaglania:
		mov		word ptr cs:[y],bx 
		call	zapalPunkt
		
	nastepnyArgument:
		inc		word ptr cs:[x]			; Kolejny argument - inkrementujemy współrzędną na osi OX
		pop		cx
		
	loop	rysuj
	
	
;--- KONIEC PROGRAMU ---;
	
koniec:
	xor	ax,ax
	int	16h  							; Czekaj na klawisz

	mov	al,3h 							; Powrót do trybu tekstowego
	mov	ah,0
	int	10h

	mov	ah,4ch  						; Koniec programu i powrót do systemu
	int	021h

;--- KONIEC DZIAŁANIA PROGRAMU ---;
	

;%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%;	
;--- PROCEDURY WCZYTYWANIA I PARSOWANIA DANYCH ---;
;%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%;

parsujZnak proc
	cmp 	al,' '					; Sprawdzamy spację
	jne 	czytajDalej
	call 	znalezionoSpacje
	jmp 	kolejnyZnak
		
	czytajDalej:
		cmp 	al,13				; Sprawdzamy koniec linii
		je 		kolejnyZnak
		cmp 	al,'$'				; Sprawdzamy dolara
		je 		kolejnyZnak
		cmp 	al,'-'				; Sprawdzamy znak ujemny
		jne 	dodatnia
		call 	ujemna				; Jeśli napotkaliśmy znak '-'
		jmp 	kolejnyZnak
		
	dodatnia:	
		cmp 	al,'.' 				; Sprawdzamy kropkę
		jne 	nieDziesietne
		cmp 	byte ptr cs:[dziesietne],1
		jl 		ustawKropke			; Jeśli zmienna pomocnicza jest mniejsza od 1 (nie było kropki) - ustawia ją
		jmp		bledneDane			; Dwie kropki w jednej liczbie - błędne dane
		
	ustawKropke:	
		mov 	byte ptr cs:[dziesietne],1 	
		jmp 	kolejnyZnak
		
	nieDziesietne:
		jge 	poprawne			; Jeśli ASCII wczytanego znaku jest większe niż ASCII kropki - może to być poprawny znak
		jmp 	bledneDane			; Błędne dane - nie wczytano cyfry / minusa / kropki
		
	poprawne:
		cmp 	al,'9'
		jle 	dobreDane			; Jeśli wprowadzono odpowiedni znak lub cyfrę
		jmp 	bledneDane
		
	dobreDane:
		cmp 	byte ptr cs:[pom],0	; Zmienna pom przechowuje informację, który współczynnik aktualnie wczytujemy
		jne 	dalejB
		call 	aktualizujA			; Jeśli pom = 0, to wczytujemy współczynnik a
		
		cmp 	byte ptr ds:[82h + bp + 1],' '	; Sprawdzamy, czy kolejny znak to spacja
		jne 	kolejnyZnak
		cmp 	byte ptr cs:[dziesietne],0		; Sprawdzamy, czy liczba ma format dziesiętny (1-tak, 0-nie)
		jne 	kolejnyZnak
		call 	domnozA							; Jeśli liczba nie ma formatu dziesiętnego - mnożymy *10
		jmp 	kolejnyZnak
		
	dalejB:								; Procedura analogiczna do powyższej
		cmp 	byte ptr cs:[pom],1	
		jne 	dalejC
		call 	aktualizujB 			; Jeśli pom = 1, to wczytujemy współczynnik b
		cmp 	byte ptr ds:[82h + bp + 1],' '
		jne 	kolejnyZnak 
		cmp 	byte ptr cs:[dziesietne],0
		jne 	kolejnyZnak
		call 	domnozB
		jmp 	kolejnyZnak
		
	dalejC:
		cmp 	byte ptr cs:[pom],2
		jne 	konczPrzypisanie 
		call	aktualizujC  			; Jeśli pom = 2, to wczytujemy współczynnik c
		cmp 	byte ptr ds:[82h + bp + 1],' '
		jne 	ostatniWspolczynnik 
		cmp 	byte ptr cs:[dziesietne],0
		jne 	ostatniWspolczynnik
		call 	domnozC
		
	ostatniWspolczynnik: 
		cmp 	byte ptr ds:[82h + bp + 1],13	; Sprawdzamy, czy kolejny znak to koniec linii
		jne 	kolejnyZnak   
		cmp 	byte ptr cs:[dziesietne],0
		jne 	kolejnyZnak
		call 	domnozC
		jmp 	kolejnyZnak
		
	konczPrzypisanie:
		cmp 	byte ptr cs:[pom],3
		jne 	kolejnyZnak 			; Jeśli pom = 3, kończymy przypisywanie argumentów
		jmp 	przypiszZmienne
		
	kolejnyZnak:
		inc		bp  
		inc		si
		ret
		
parsujZnak endp
	
	
znalezionoSpacje proc
	push	ax
	mov 	byte ptr cs:[dziesietne],0		; Zerujemy zmienną dziesiętną - wczytywana liczba będzie najpierw całkowita
	
	mov 	byte ptr cs:[znak],1			; Zmienna znaku z domyślną wartością dodatnią
	pop		ax
	
	cmp		byte ptr ds:[82h + bp - 1],' '	; Sprawdzamy, czy poprzedni znak był spacją
	jne		pierwszaPoArgumencie			; Jeśli nie, jest to pierwsza spacja po argumencie (koniec współczynnika) i możemy przejść do kolejnego
	ret										; Jeśli tak, jest to kolejna spacja z kolei i nadal jesteśmy przy tym samym współcznniku
	
	pierwszaPoArgumencie:
		inc 	byte ptr cs:[pom]			; Zwiększamy wartość zmiennej pom - kolejny współczynnik
		ret
znalezionoSpacje endp
	
	
ujemna proc
	mov 	byte ptr cs:[znak],-1
	ret
ujemna endp	


aktualizujA proc
	cmp 	byte ptr cs:[dziesietne],2		; Jeśli jest to co najmniej druga cyfra po przecinku, ignorujemy
	jl  	aktA
	ret
	
	aktA: 
		cmp 	byte ptr cs:[dziesietne],1	; Jeśli poprzednim znakiem była kropka
		jl   	przypiszA
		inc 	byte ptr cs:[dziesietne]	; Zwiększamy zmienną
		
	przypiszA:
		mov 	bl,al 						; Zapamiętujemy obecny znak w bl
		mov 	al,byte ptr cs:[a]
		mov 	cl, 10
		imul 	cl
		mov 	byte ptr cs:[a],al			; Podstawiamy a := a*10
		sub 	bl,48						; Konwertujemy ASCII znaku na jego wartość rzeczywistą
		cmp 	byte ptr cs:[znak],1		; Jeśli znak = 1, to daną liczbę przyjmujemy jako dodatnią
		je  	plusA
		sub 	byte ptr cs:[a],bl			; Liczba ujemna - odejmujemy wartość
		ret
	plusA:
		add byte ptr cs:[a],bl 				; Liczba dodatnia - dodajemy wartość
		ret
aktualizujA endp
	

aktualizujB proc							; Analogiczna do powyższej procedury
    cmp 	byte ptr cs:[dziesietne],2
    jl 		aktB
    ret
	
	aktB: 
		cmp 	byte ptr cs:[dziesietne],1
		jl 		przypiszB
		inc 	byte ptr cs:[dziesietne]
   
	przypiszB:   
		mov 	bl,al
		mov 	al,byte ptr cs:[b]
		mov 	cl, 10
		imul 	cl
		mov 	byte ptr cs:[b],al
		sub 	bl,48
		
		cmp 	byte ptr cs:[znak],1
		je 		plusB
		sub 	byte ptr cs:[b],bl
		ret
	plusB:	
		add 	byte ptr cs:[b],bl
		ret
aktualizujB endp


aktualizujC proc
    cmp 	byte ptr cs:[dziesietne],2
    jl  	aktC
    ret
	
	aktC: 
		cmp 	byte ptr cs:[dziesietne],1
		jl  	przypiszC
		inc 	byte ptr cs:[dziesietne]
	   
	przypiszC:	
		mov 	bl,al
		mov 	al,byte ptr cs:[wc]
		mov 	cl, 10
		imul 	cl
		mov 	byte ptr cs:[wc],al
		sub 	bl,48
		cmp 	byte ptr cs:[znak],1
		je  	plusC
		sub 	byte ptr cs:[wc],bl
		ret
		
	plusC:	
		add 	byte ptr cs:[wc],bl
		ret
aktualizujC endp


domnozA proc
	mov 	al, byte ptr cs:[a]
	mov 	cl,10
	imul 	cl
	mov 	byte ptr cs:[a],al				; Podstawiamy a := a*10
	ret
domnozA endp
	
domnozB proc
	mov 	al, byte ptr cs:[b]
	mov 	cl,10
	imul 	cl  
	mov 	byte ptr cs:[b],al
	ret
domnozB endp
	
domnozC proc
	mov 	al, byte ptr cs:[wc]
	mov 	cl,10
	imul 	cl
	mov 	byte ptr cs:[wc],al
	ret
domnozC endp


bezArgumentow:
	mov		dx,offset bezArg
	mov		ax,seg bezArg
	mov		ds,ax
	mov		ah,9 
	int		21h					; Wypisuje komunikat o braku argumentów
	
	mov		ah,4ch  			; Kończy program i wraca do systemu
	int		21h
	
	
bledneDane:
	mov		ax,seg bledneArg
	mov		ds,ax
	mov		dx,offset bledneArg
	mov		ah,9 
	int		21h					; Wypisuje komunikat o błędnych argumentach
	
	mov		ah,4ch
	int		21h	

	
;%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%;
;--- PROCEDURY RYSOWANIA WYKRESU ---;
;%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%;

zapalPunkt proc
	mov		ax,0a000h  			; Adres segmentu pamięci obrazu
	mov		es,ax
	mov		ax,word ptr cs:[y]
	mov		bx,320  			; Ilość pikseli w linii obrazu, offset punktu liczymy od lewej do prawej, kolejnymi liniami, (0;0) w lewym górnym rogu
	imul	bx					; DX:AX := AX*BX
	add		ax,word ptr cs:[x]  ; Obliczamy punkt do zapalenia: ax = 320*y + x
	mov		bx,ax				; Offset punktu w bx
	mov		al,byte ptr cs:[kolor]	; Kolor w al
	mov		byte ptr es:[bx],al		; zapisujemy Kolor w danym offsecie
	ret
zapalPunkt endp


liczWartosc proc
	mov 	dx,0
	mov		ax,word ptr cs:[x]
	sub 	ax,160 					; Przesunięcie x, aby wskazywał rzeczywistą wartość na osi, nie współrzędną punktu (zakres -160:160, nie 0:320)
	imul	ax						; DX:AX = AX * AX
	mov 	bx,word ptr cs:[osta]	; Współczynnik przy x^2
	imul	bx						; DX:AX = AX*BX
	mov 	bx,100 					; Zwiększanie skali dla mniejszych liczb
	idiv 	bx						; AX = DX:AX / BX

	mov 	bx, ax					; Wynik zapisujemy w bx
	mov 	ax, word ptr cs:[x]		; Ponownie to samo dla współczynnika b przy pierwszej potędze
	sub 	ax,160
	mov 	cx,word ptr cs:[ostb]
	imul 	cx
	mov 	cx,10 					; Ponownie zwiększamy skalę dla małych liczb
	idiv 	cx						; AX = DX:AX / CX
	add 	ax,bx					; Dodajemy do wyniku, rezultat w ax
	
	add 	ax, word ptr cs:[ostc] 	; Dodajemy wyraz wolny, obliczona wartość w ax
	ret
liczWartosc endp

	
uciaglanie proc
	cmp 	ax,0
	jg  	rosnaco		; Skok do uciąglania dla funkcji rosnącej
	mov 	cx,-1		; Jeśli zawartość ax jest mniejsza od 0, to mnożymy tę liczbę przez -1
	imul 	cx
	
	mov 	cx,ax
	uciaglijMalejaco:	; Uciąglanie dla funkcji malejącej - dla danego x zapalamy punkty mniejsze od wartości funkcji dla poprzedzających wartości x
		push 	cx
		dec 	word ptr cs:[y]
		call 	zapalPunkt
		pop 	cx
		loop 	uciaglijMalejaco
		
		jmp 	zakonczUciaglanie

	rosnaco:
		mov 	cx,ax
		
	petlauciaglajaca2:	; Uciąglanie funkcji rosnącej - analogicznie jak dla funkcji malejącej
		push 	cx
		inc 	word ptr cs:[y]
		call 	zapalPunkt
		pop 	cx
		loop 	petlauciaglajaca2
	
	zakonczUciaglanie:
		ret
uciaglanie endp


;%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%;
;--- ZMIENNE W SEGMENCIE KODU ---;
;%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%;

x			dw	0 		; współrzędna OX
y			dw	0 		; współrzędna OY
kolor		db	0

a 			db 0 		; Współczynnik przy x^2 używany podczas wczytywania
b 			db 0 		; Współczynnik przy x używany podczas wczytywania
wc 			db 0 		; Wyraz wolny używany podczas wczytywania

osta 		dw 0 		; Współczynnik przy x^2 używany podczas rysowania
ostb 		dw 0 		; Współczynnik przy x używany podczas rysowania
ostc 		dw 0 		; Wyraz wolny używany podczas rysowania

pom 		db 0 		; Zmienna pomocnicza do zliczania, który współczynnik wczytujemy
znak 		db 1 		; Zmienna przechowująca znak wczytywanego współczynnika (1 - dodatni, -1 - ujemny)
dziesietne 	db 0 		; Zmienna pomocnicza do zliczania i ignorowania miejsc dziesiętnych (od drugiego)


code ends

;%%%%%%%%%%%%%%%%%%%%%;
;--- SEGMENT STOSU ---;
;%%%%%%%%%%%%%%%%%%%%%;

mystack segment stack
		dw 200 dup(?)
	top	dw ?
mystack ends

end start