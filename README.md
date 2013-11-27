jnny.rttn
=========
Członkowie zespołu: Paweł Pietrzykowski nr 91823 <jnny.rttn@gmail.com>

Aplikacja mobilna na platformę Android. Użytkownik poprzez udostępnienie swojej aktualnej lokalizacji
gps prześle ją poprzez wywołanie REST do serwera i pobierze najbliższe punkty apteczne. Dodatkowo użytkownik
będzie mógł poprzez pole tekstowe formularza wysłać zapytanie do serwera dla dowolnej ulicy o ile zostanie 
ona odpowiednio przekonwertowana. Szukana lokalizacja zostanie poprzez obiekt google.maps.Geocoder przekonwertowana odpowiednio na
szerokość (lat) i długość (long). Część serwerowa obsługiwana przez framework Yii lub własny mini framework MVC.

Założona funkcjonalność po pierwszym etapie:

1 Po pierwszym etapie użytkownik będzie miał możliwość wprowadzenia poprzez pole formularza ulicy w pobliżu której chciałby odnaleźć Aptekę.
2 Wybrana lokalizacja pojawi się w mapie Google wraz z zaznaczonymi znalezionymi w pobliżu aptekami. ( Google Maps Android API )

Zakładana funkcjonalność wersji końcowej:

1 Możliwość wprowadzenia poprzez pole formularza ulicy w pobliżu której chciałby odnaleźć Aptekę. 
2 Wybrana lokalizacja pojawi się w mapie Google wraz z zaznaczonymi znalezionymi w pobliżu aptekami. ( Google Maps Android API )
3 Wykorzystanie aktualnego położenia użytkownika.
4 Aktywne markery na mapie google - kliknięcie spowoduje wyświetlenie szczegółowego opisu apteki.

Opis architektury:

Aplikacja działająca na urządzeniach mobilnych poprzez wywołania REST komunikuje się z działającym na serwerze backendem w PHP. Pobrana z 
formularza nazwa ulicy konwertowana jest na dane long i lat lub dane z GPS  przekazywane są do kontrolera obsługującego wywołania 
GET, POST, PUT, DELETE. Kontroler pobiera następnie dane z modelu który komunikuje się z bazą danych mongoDB i w zależności od wartości danych 
przekazanych przez kontroler - zwraca tablicę BSON z poszukiwanymi danymi które są wyświetlane w widoku aplikacji mobilnej.

Przewidywane środowisko realizacji projektu

- System Operacyjny Android 
- Google Maps Android API
- Yii Framefork lub inny framework PHP (MVC)
- mongodb 2.4
- RESTfull api.
- System Operacyjny Debian Linux.

Przewidywane trudności i problemy

- Kompatybilność z różnymi wersjami systemu android ( szczególnie starszymi )
