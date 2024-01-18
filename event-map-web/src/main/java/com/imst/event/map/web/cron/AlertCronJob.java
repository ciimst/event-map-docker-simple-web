//package com.imst.event.map.web.cron;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.imst.event.map.web.constant.MultitenantDatabaseE;
//import com.imst.event.map.web.constant.Statics;
//import com.imst.event.map.web.services.CallableAlertService;
//import com.imst.event.map.web.utils.IPUtils;
//import com.imst.event.map.web.vo.DataSourceInfo;
//
//import lombok.extern.log4j.Log4j2;
//
//@Component
//@Log4j2
//public class AlertCronJob {
//	
//	@Autowired @Qualifier("masterDataSource") DataSource masterDataSource;
//	
//	@Scheduled(initialDelay = 1000, fixedDelay = 1000) // 1 saniye
//	public void alertCheck() {
//		
//		try {
//			
//			if( // bu iplere sahip bilgisayarlarda çalışmaz
//					IPUtils.isIpContaining("177.177.0.123") 
//					|| IPUtils.isIpContaining("192.168.1.10") 
//					|| IPUtils.isIpContaining("177.177.0.183")
//					|| IPUtils.isIpContaining("192.168.17.1")
//					|| IPUtils.isIpContaining("177.177.0.239")
////					|| IPUtils.isIpContaining("177.177.16.17")
//					) {
//				return;
//			}
//
//			// Uygulama açılışında ekstra veritabanları configürasyon dosyasından okunup ayaklandırılmış ve isimleri static bir listede tutulmuştu.
//			// bu statik liste kullanılarak her bir veritabanı için bir thread açılır, ve callable classının içerisine yazılmaya başlanır.
//			// buradaki işlemlerin çoğu thread içerisinde gerçekleştirileceği için default veritabanı içinde bir thread açılması sağlanmalıdır.
//			// thread içerisinde her bir thread kendi veritabanı ismini bildiği için herbiri kendi sorgusunu alert_state tablosuna atar ve son kaldığı idyi öğrenir.
//			// Eğer tabloda ilgili veritabanına ait bir kayıt bulunmuyor ise tabloya bir kayıt eklenir ve lastid olarak event tablosundaki en büyük id çekilip buraya yazılır.
//			// Bunun amacı yeni eklenen bir veritabanının eski olaylarını önemsemeden burdan sonraki kayıtları için alarm üretilmesi sağlanır.
//			// bu durumda elimizde veritabanları için son olay idleri elde etmiş oluruz.
//			// daha sonra event tablosundan veritabanlarındaki son idden sonraki olayları sorgulanması sağlanır. 
//			// limit olarak 100 - 200 gibi rakamlar kullanılabilir. uzun sürüyor ise azaltılabilir, çünkü diğer threadlerde eleman azsa bu threadi bekliyor olacaklardır.
//			// eventler geldiğinde yine callable dosyasının içerisinden devam ediyoruz her bir thread kendi işlemini gerçekleştirir.
//			// veritabanındaki alert tablosuna her bir event için sorgu atmaya başlar. bu sorguda elimizde bulunan lat ve long değerlerinden bir polygon oluşturulur. SpatialUtil classı kullanılarak
//			// daha sonra bu veri ile beraber spatial sorgu AlertRepository için oluşturulur
//			// ve sorgu atılır. Sorgudan gelen cevaplar AlertEvent Tablosuna kaydedilir.
//			// sorguda eventin hangi layerda olduğu önemli olabilir. buna göre sorgu atılabilir
//			// Bu işlem bütün eventler için uygulanır.
//			// işlemler bittikten sonra alert_state tablosuna son işlenen event_id last_id olarak kaydedilir.
//			// böylece alert ile ilgili çalışma tamamlanmış olur
//			
//			ExecutorService executor = Executors.newFixedThreadPool(Statics.tenantDataSourceInfoMap.size() + 1);
//			
//    		CallableAlertService callableAlertService = new CallableAlertService(new DataSourceInfo(Statics.DEFAULT_DB_NAME, MultitenantDatabaseE.MASTER, masterDataSource) );
//	    	executor.submit(callableAlertService);
//
//	    	for (DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
//				
//	    		callableAlertService = new CallableAlertService(dataSourceInfo);
//		    	executor.submit(callableAlertService);
//			}
//	    	
//	    	executor.shutdown();
//	    	executor.awaitTermination(5, TimeUnit.MINUTES);// işlemlerin en fazla 5 dakika sürmesine izin verilir
//
//		}
//		catch (Exception e) {
//			
//			log.catching(e);
//		}
//		
//	}
//}
