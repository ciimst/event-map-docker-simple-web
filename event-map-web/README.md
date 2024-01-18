## Docker Image Ayarları ##
  Image'ları import etmek için Docker dosyalarının bulunduğu ana dizine girilir ve aşağıdaki komutlar sırasıyla çalıştırılır.

  - `sudo docker load -i docker-image/postgis.tar`
  - `sudo docker load -i docker-image/eventmap.tar`

  Image'lar oluşturulduktan sonra container'lar oluşturulmalıdır. Container'ları oluşturmak ve projeyi başlatmak için aşağıdaki komut girilmelidir.

  
  `sudo docker compose up`

## PostgreSQL Ayarları ##
Docker container olarak load edilen postgresql veri tabanı yerine harici bir veri tabanı kullanılmak isteniyorsa aşağıdaki adımlar takip edilmelidir.
postgis.tar docker image dosyası load edilmemelidir.

	- applicationAdmin.properties ve applicationWeb.properties dosyalarında bulunan aşağıdaki alanlar değiştirilmesi gerekmektedir.

		master.datasource.jdbc-url=veri tabanı adresi									 
		master.datasource.username=veri tabanı kullanıcı adı
		master.datasource.password=veri tabanı şifresi

	- docker-compose.yml dosyasındaki;
		"postgis:" tagı ile beraber altındaki herşey silinecektir
		"networks:" tagı görüldüğü heryerde altındaki herşey ile beraber silinecektir
**Dump**
	: Eğer dump dosyasından veritabanına sıfırdan alınması gerekiyorsa kurulum dosyaları içerisinde bulunan Sql Scripts klasörünün altındaki dumb klasörü içerisinde bulunan dump dosyası kullanılabilir. Bu dump dosyası PgAdmin4 v6.1 uygulaması ile alınmıştır. (NOT: Eğer bu işlem daha önce yapıldıysa tekrar gerekmemektedir. Incremental olarak sağlanan scriptler üzerinden ilerlenmesi gerekmektedir.)
	
**Incremental Scripts**
  : Kurulum versiyonları arasında veri tabanında gerçekleşmiş olan değişiklikler Kurulum dosyaları içerisinde Sql Scripts klasörü içerisinde bulunan "Incremental Scripts.txt" dosyası içerisinde script halinde sağlanmaktadır. Bu scriptler hazır olan veri tabanına uygulanarak yeni sürüm için gerekli olan verilerin aktarılması sağlanmış olur
  
**Sıfır Veritabanı**
  : Sıfır veritabanı için dump klasörü altındaki "empty" backup dosyası kullanılabilir. Uygulamalar bu veritabanı üzerinden ayakladırıldıktan sonra Yönetici Panelinden "admin" kullanıcı adı ve "admin" şifresi ile giriş yapılıp bütün tanımlamalar sıfırdan gerçekleştirilebilir.
  



## Şifre Encription Yöntemi ##
Application.properties dosyaları içerisindeki veri tabanı şifrelerinin encripted bir şekilde tutulması isteniyorsa aşağıdaki adımlar takip edilmelidir.
	Öncelikle veri tabanı şifresi açık bir şekilde yazılarak proje normal olarak başlatılır.
	Daha sonra yönetim panelinde Ayarlar menüsü altındaki `Şifreleme İşlemi` sayfası açılır.
	Bu sayfada veri tabanı şifresi encript edilir.
	Encript edilen şifre kopyalanarak Application.properties dosyaları içerisinde bulunan veri tabanı şifreleri alanına `ENC()` tagı içerisine yerleştirilerek yazılır. Daha sonra docker container yeniden başlatılır. Projenin normal bir şekilde ayaklanması beklenir.
	
  **Örnek**
  : master.datasource.password=ENC(z7WGikU1vxdwocy5QoKid7LSkNyemQrT5ArNSZD1KpjGB1O2U+xVF0UfBjDETxhO)

## SSL Ayarları ##

Projenin SSL sertificalı olarak çalışması için gerekli olan ayarlar Application.properties dosyalarındaki SSL Configuration kısmında düzenlenmektedir. 
	Aşağıdaki parametrelerle SSL konfigürasyonu gerçekleştirilmektedir.	
	Hazır bir sertifika kullanılıyor ise `server.ssl.key-alias` satırı yorum satırına alınmalıdır.
	Eğer SSL konfigürasyonu iptal edilmek istenirse `server.ssl.enabled` değeri false yapılmalıdır ve browser üzerinden uygulama açılırken `https` yerine `http` olarak çağrılmalıdır.
	
## SSL Configuration ##
`server.ssl.enabled=true` SSL özelliğinin açık veya kapalı olması buradan belirlenebilmektedir. true ve false değerlerini almaktadır
	
  
  - The format used for the keystore. It could be set to JKS in case it is a JKS file.
  Key store türünü belirtmektedir.
  `server.ssl.key-store-type=PKCS12`
    
    
  - The path to the keystore containing the certificate.
    Oluşturulan keystore sertifika dosyasının yolunu belirtmektedir.
    `server.ssl.key-store=/usr/local/eventmap/web/cacerts.p12`
    
  - The password used to generate the certificate
    Oluşturulan keystore dosyasının şifresini belirtmektedir.
    `server.ssl.key-store-password=changeit` 
    
   - He alias mapped to the certificate
    Key store dosyasına eklenen kendi uygulamamıza verdiğimiz ismi belirtmektedir. Hazır sertifika kullanılıyor ise bu alan yorum satırına alınmalıdır. keystore içerisinde tek bir sertifika varsa yine kullanılmasına ihtiyaç yoktur.
    `server.ssl.key-alias=myapp`
	

## SSL sertifikası ##
	
Uygulamanın ssl destekli olarak çalışabilmesi için SSL sertifikasına ihtiyaç duyulmaktadır. Güvenilir SSL sertifikası sağlayıcılarından sertifikalar alınıp kullanılabilir. Eğer SSL sertifikası oluşturulmak isteniyorsa aşağıdaki adımlar takip edilmelidir.
	aşağıdaki script ile sıfırdan ssl sertificası oluşturulabilmektedir 
	
	keytool -genkeypair -alias myappssl -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore cacerts.p12 -validity 3650
	
İlgili dosya Proje içerisinde resource klasörü altındaki keystore klasörü içerisine yerleştirilmelidir. docker containerları için cacerts.p12 ismi ile volume dosyası olarak docker klasörü içerisine yerleştirilmelidir.
Aynı dosya web ve admin projeleri için ortak olarak kullanılabilir

## SAML Ayarları ##
Kurulum dosyaları içerisinde 2 adet web uygulaması paylaşılmaktadır. Bunlardan saml entegrasyonu ile çalışması için uygun olarak hazırlanmış olanın adı `event-map-web.jar` şeklindedir. Diğeri ise saml entegrasyonu olmadan veri tabanı ile çalışacak şekilde ayarlanmıştır ve adı `event-map-web-db.jar` şeklinde ayarlanmıştır. Eğer saml entegrasyonu yerine veri tabanı üzerindeki kullanıcılar ile giriş yapılması istenirse `event-map-web-db.jar` jar dosyasının ismi `event-map-web.jar` olarak değiştirilir ve docker container yeniden başlatılır. 
	
SAML konfigürasyonu ile işlem yapmak için applicationWeb.properties dosyası içerisindeki `SAML Configuration` bilgileri ve `LDAP Configuration` bilgilerinin tamamlanmış olması gerekmektedir.
	
SAML konfigürasyonunda şifreleme metodu olarak SHA1, SHA256 gibi değerler seçilebilmektedir. Bu değerler seçildiğinde adfs tarafında yapılan konfigürasyonda advanced sekmesinde yine aynı metod seçilmelidir. Bu metod application properties tarafında seçildikten sonra veya kod içerisinde değiştirildikten sonra https://localhost:8443/saml/metadata adresindeki xml dosyası indirilerek adfs sunucusunda "Relying Party Trust" ekleme işlemi gerçekleştirilmelidir. Ekleme işleminden sonra Encription sekmesinden View butonu ile sertifika görüntülenip Detay sekmesinden "Signiture algorithm" özelliği kontrol edilebilir. bu değerde aynı metodu gösteriyor olması gerekmektedir. örnek olarak "sha256RSA" yazıyor olabilir.
	
Relying Party Trust eklendikten sonra sağ tıklayarak kural eklemek gerekebilmektedir. "Edit Claim Rules" kısmından yeni kural ekle işlemi gerçekleştirilebilir. Uygulama kullanıcı adı için "Name ID" değerinin dolu gelmesini beklemektedir ve buradan gelen değerin kullanıcı adı olmasını beklemektedir. Bu amaçla kural eklenerek "SAM-Account-Name" bilgisi "Name ID" bilgisine aktarılabilir. Normalde "Name ID" alanına kullanıcı adı bilgisi geliyorsa bu işlemin yapılmasına ihtiyaç duyulmamaktadır.
	
Saml seftifikasinin adfs tarafinda sifrelenme metodunu ayarlamak için "saml.keystore.encryption.method=SHA256" özelliği kullanılır. default metod SHA256'dir. Diger metodlar: SHA1, SHA256, SHA384, SHA512

## Log takibi ##
Programlar çalıştıktan sonra bir hata meydana gelirse kendi loglarına yazacaktır. Loglar docker container içerisinde `/event-map/logs` klasörü altında bulunmaktadır. Loglar buradan takip edilebilir.	

## LDAP sertifikası ##
- LDAP kullanıcıları sisteme otomatik dahil edilirler ve default olarak seçilmiş olan projile atanırlar. Eğer default profil seçilmemiş ise LDAP kullanıcıları sisteme otomatik dahil olamazlar.
- LDAP sertifikası ile ilgili bilgi için LDAP dökümanına bakınız
	
## SAML Sertifikası ##
- SAML sertifikası ile ilgili bilgi için SAML dökümanına bakınız. SAML için oluşturulan sertifika ldap için de kullanılmaktadır.
