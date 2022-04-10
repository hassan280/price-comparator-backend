package com.webScraping.demo.service;

import java.io.IOException;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.parser.Part.IgnoreCaseType;
import org.springframework.stereotype.Service;

import com.webScraping.demo.model.RequestDTO;
import com.webScraping.demo.model.ResponseDTO;
import com.webScraping.demo.repositories.RequestDTORepository;
import com.webScraping.demo.repositories.ResponseDTORepository;


@Service
public class ScraperServiceImpl implements ScraperService {

    @Value("#{'${website.urls}'.split(',')}")
    List<String> urls;
    
    @Autowired
    private RequestDTORepository requestDTORepository;
    @Autowired
    private ResponseDTORepository responseDTORepository;
    
    public static boolean containsAllWords(String word, String ...keywords) {
        for (String k : keywords)
            if (!word.toLowerCase().contains(k.toLowerCase())) return false;
        return true;
    }

    @Override
    public Set<ResponseDTO> getVehicleByModel(String vehicleModel) {
    	
    	
        Set<ResponseDTO> responseDTOS = new HashSet<>();

        
        for (String url: urls) {

            if(url.contains("amazon")) {
            	
            	String[] parts = vehicleModel.split("&");
        		String part1 = parts[0];
        		String part2 = parts[1];
        		for (int j = 1; j < 20; j++) {
        			
        			if (vehicleModel.contains("&")&& (!part2.equalsIgnoreCase("ALL"))) {
                		
                		if (vehicleModel.contains(" ")) {
                			extractDataFromAmazon(responseDTOS,url + part1.replaceAll(" ", "+")+"&i="+part2+"-intl-ship&page="+j, vehicleModel);
    					}else
    						extractDataFromAmazon(responseDTOS, url + part1+"&i="+part2+"-intl-ship&page="+j, vehicleModel);
    				}
                	else if(vehicleModel.contains(" "))
                		extractDataFromAmazon(responseDTOS, url + vehicleModel.replaceAll(" ", "+")+"&page="+j, vehicleModel);
                	else
                		extractDataFromAmazon(responseDTOS, url + vehicleModel+"&page="+j, vehicleModel);
				}
            	
            }else if(url.contains("ebay")) {
            	
            	String subURL=vehicleModel;
            	for (int j = 1; j < 10; j++) {
            	if(vehicleModel.contains("&"))
            		subURL=vehicleModel.split("&")[0];
            	if(vehicleModel.contains(" "))
            		subURL=subURL.replaceAll(" ", "+");
            	extractDataFromEbay(responseDTOS, url + subURL+"&_pgn="+j, vehicleModel.split("&")[0]);
            	}
            	
            }else if(url.contains("walmart")) {
            	
            	String subURL=vehicleModel;
            	for (int j = 1; j < 10; j++) {
            	if(vehicleModel.contains("&"))
            		subURL=vehicleModel.replaceAll("&", "+");
            	if(vehicleModel.contains(" "))
            		subURL=subURL.replaceAll(" ", "+");
            	System.out.println(subURL);
            	
            	extractDataFromWalmart(responseDTOS, url + subURL+"&page="+j, vehicleModel.split("&")[0]);
            	}
            	
            }else if(url.contains("alibaba")) {
            	
            	String subURL=vehicleModel;
            	for (int j = 1; j < 10; j++) {
            	if(vehicleModel.contains("&"))
            		subURL=vehicleModel.replaceAll("&", "+");
            	if(vehicleModel.contains(" "))
            		subURL=subURL.replaceAll(" ", "+");
            	System.out.println(subURL);
            	System.out.println(url+subURL);
            	extractDataFromAlibaba(responseDTOS, url + subURL+"page="+j, vehicleModel.split("&")[0]);
            }
            }

        }

        
        return responseDTOS;
    
    	}
    
    
private void extractDataFromAlibaba(Set<ResponseDTO> responseDTOS, String url,String vehicleModel) {


        
        if(requestDTORepository.findByKeyWord(vehicleModel)==null) {
        RequestDTO request=null;	
        request=new RequestDTO(vehicleModel);
        requestDTORepository.save(request);
        }
        try {
            Document document = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.38 Safari/537.36")
                    .get();

            System.out.println(url);
            Elements elements;
            elements = document.getElementsByClass("list-no-v2-outter J-offer-wrapper");
            
            
            for (Element ads: elements) {
                    ResponseDTO responseDTO = new ResponseDTO();
                    
                    if (!StringUtils.isEmpty(ads.child(0).text())
                    		&& containsAllWords(ads.child(0).text(), vehicleModel.split(" "))) {
                    	
                        responseDTO.setTitle(ads
                        		.getElementsByClass("elements-title-normal__content large")
                        		.html().replace("<strong>", "").replace("</strong>", ""));
                        
                        
                        responseDTO.setImage("https:"+ads.getElementsByClass("seb-img-switcher__imgs").attr("data-image"));
                        
                        
                        if(!StringUtils.isEmpty(ads.getElementsByClass("elements-offer-price-normal__promotion").text())
                        	) {
                        responseDTO.setPrice((ads.getElementsByClass("elements-offer-price-normal__promotion")
                        		.html().replaceAll("[$,]", "")));
                        }
                        else if(!StringUtils.isEmpty(ads.getElementsByClass("elements-offer-price-normal__price").html())) {
                        	
                        	responseDTO.setPrice((ads.getElementsByClass("elements-offer-price-normal__price")
                            		.text().replaceAll("[$,]", "").split("-")[0]));
                        }

                        responseDTO.setUrl(
                        		"https:"+ads
                        		.getElementsByClass("elements-title-normal one-line")
                        		.attr("href"));
                        responseDTO.setStore("Alibaba");
                        
                    }
                    if (responseDTO.getUrl() != null && responseDTO.getPrice()!=null) {
                    	
                    	String s=responseDTO.getTitle();
                    	String p=responseDTO.getPrice();
                    	ResponseDTO r=responseDTORepository.findByTitle(s);
                    			
                    	if(r==null) {
                    		responseDTOS.add(responseDTO);
                    		responseDTORepository.save(responseDTO);}
                    	else if(!r.getPrice().equals(p)) {
                    		r.setPrice(responseDTO.getPrice());
                    		responseDTOS.add(r);
                    		}
                    	else
                    		responseDTOS.add(r);
                    	
                    	
                    }
                    
                }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


private void extractDataFromWalmart(Set<ResponseDTO> responseDTOS, String url,String vehicleModel) {


        
        if(requestDTORepository.findByKeyWord(vehicleModel)==null) {
        RequestDTO request=null;	
        request=new RequestDTO(vehicleModel);
        requestDTORepository.save(request);
        }
        try {
            Document document = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.38 Safari/537.36")
                    .get();

            System.out.println(url);
            
            Elements elements;
            
            elements = document.getElementsByClass("mb1 ph1 pa0-xl bb b--near-white w-25");
            //System.out.println(elements);
            
            for (Element ads: elements) {
                    ResponseDTO responseDTO = new ResponseDTO();
                    
                    if (!StringUtils.isEmpty(ads.child(0).text())
                    		&& containsAllWords(ads.child(0).text(), vehicleModel.split(" "))) {
                    	
                        responseDTO.setTitle(ads
                        		.getElementsByClass("f6 f5-l normal dark-gray mb0 mt1 lh-title")
                        		.first().text());
                        responseDTO.setImage(ads.getElementsByTag("img").attr("src"));
                        
                        
                        if(!StringUtils.isEmpty(ads.getElementsByClass("b f5 f4-l black mr1 lh-copy").text())
                        		) {
                        
                        responseDTO.setPrice((ads.getElementsByClass("b f5 f4-l black mr1 lh-copy")
                        		.html().replaceAll("[From $,]", "")));
                        }else if(!StringUtils.isEmpty(ads
                        		.getElementsByClass("mb1 ph1 pa0-xl bb b--near-white w-25").first()
                        		.getElementsByClass("b black f5 mr1 mr2-xl lh-copy f4-l").text())) {
                        	
                        	responseDTO.setPrice((ads
                            		.getElementsByClass("mb1 ph1 pa0-xl bb b--near-white w-25").first()
                            		.getElementsByClass("b black f5 mr1 mr2-xl lh-copy f4-l")
                            		.html().replaceAll("[$,]", "")));
                        }
                        responseDTO.setUrl(
                        		"https://www.walmart.com"+ads
                        		.getElementsByClass("mb1 ph1 pa0-xl bb b--near-white w-25").first()
                        		.getElementsByClass("absolute w-100 h-100 z-1")
                        		.attr("href"));
                        responseDTO.setStore("Walmart");
                        
                    }
                    if (responseDTO.getUrl() != null && responseDTO.getPrice()!=null) {
                    	String s=responseDTO.getTitle();
                    	String p=responseDTO.getPrice();
                    	ResponseDTO r=responseDTORepository.findByTitle(s);
                    			
                    	if(r==null) {
                    		responseDTOS.add(responseDTO);
                    		responseDTORepository.save(responseDTO);}
                    	else if(!r.getPrice().equals(p)) {
                    		r.setPrice(responseDTO.getPrice());
                    		responseDTOS.add(r);
                    		}
                    	else
                    		responseDTOS.add(r);
                    }
                    
                }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
private void extractDataFromEbay(Set<ResponseDTO> responseDTOS, String url,String vehicleModel) {


        
        if(requestDTORepository.findByKeyWord(vehicleModel)==null) {
        RequestDTO request=null;	
        request=new RequestDTO(vehicleModel);
        requestDTORepository.save(request);
        }
        try {
            Document document = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.38 Safari/537.36")
                    .get();

            System.out.println(url);
            
            Elements elements=null;
            Element e=document.getElementById("srp-river-results");
            
            elements = e.getElementsByTag("li");
            
            for (Element ads: elements) {
                    ResponseDTO responseDTO = new ResponseDTO();
                    
                    if (!StringUtils.isEmpty(ads.child(0).text()) 
                    		&&ads.className().equals("s-item s-item__pl-on-bottom")
                    		&& containsAllWords(ads.child(0).text(), vehicleModel.split(" "))) {
                    	
                        responseDTO.setTitle(ads
                        		.getElementsByClass("s-item__title")
                        		.first().text());
                        responseDTO.setImage(ads.getElementsByTag("img").attr("src"));
                        
                        if(!StringUtils.isEmpty(ads.getElementsByClass("s-item__price").html())
                        		&& !StringUtils.isEmpty(ads.getElementsByClass("s-item__detail s-item__detail--primary")
                        				.first()
                        				.getElementsByClass("s-item__price").text())) {
                        	
                        	if (ads.getElementsByClass("s-item__detail s-item__detail--primary")
                            		.first().getElementsByClass("s-item__price").html().contains("</span>")) {
                        		
                        		responseDTO.setPrice((ads.getElementsByClass("s-item__detail s-item__detail--primary")
                                		.first().getElementsByClass("s-item__price").html()
                                		.split("<span")[0].replaceAll("[$,]", "")));
                        		
							}else
                        responseDTO.setPrice((ads.getElementsByClass("s-item__detail s-item__detail--primary")
                        		.first().getElementsByClass("s-item__price").html().replaceAll("[$,]", "")));
                        }
                        responseDTO.setUrl(
                        		ads
                        		.getElementsByClass("s-item__link")
                        		.attr("href"));
                        responseDTO.setStore("Ebay");
                        
                    }
                    if (responseDTO.getUrl() != null && responseDTO.getPrice()!=null) {
                    	String s=responseDTO.getTitle();
                    	String p=responseDTO.getPrice();
                    	ResponseDTO r=responseDTORepository.findByTitle(s);
                    			
                    	if(r==null) {
                    		responseDTOS.add(responseDTO);
                    		responseDTORepository.save(responseDTO);}
                    	else if(!r.getPrice().equals(p)) {
                    		r.setPrice(responseDTO.getPrice());
                    		responseDTOS.add(r);
                    		}
                    	else
                    		responseDTOS.add(r);
                    }
                    
                }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    private void extractDataFromAmazon(Set<ResponseDTO> responseDTOS, String url,String vehicleModel) {


        
        if(requestDTORepository.findByKeyWord(vehicleModel)==null) {
        RequestDTO request=null;	
        request=new RequestDTO(vehicleModel);
        requestDTORepository.save(request);
        }
        try {
            Document document = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.38 Safari/537.36")
                    .get();

            System.out.println(url);
            Elements elements;
            if (vehicleModel.contains("&")) {
            	
            	String[] parts = vehicleModel.split("&");
        		vehicleModel = parts[0];
        		
        		if(vehicleModel.contains(" "))
        			elements=document.getElementsByClass("s-card-container s-overflow-hidden aok-relative s-expand-height s-include-content-margin s-latency-cf-section s-card-border");
        		else
                 elements = document.getElementsByClass("s-card-container s-overflow-hidden aok-relative s-include-content-margin s-latency-cf-section s-card-border");
            }
            else {
                 elements = document.getElementsByClass("s-card-container s-overflow-hidden aok-relative s-include-content-margin s-latency-cf-section s-card-border");
            }
            
            for (Element ads: elements) {
                    ResponseDTO responseDTO = new ResponseDTO();
                    
                    if (!StringUtils.isEmpty(ads.child(0).text()) 
                    		&& containsAllWords(ads.child(0).text(), vehicleModel.split(" "))) {
                    	
                        responseDTO.setTitle(ads
                        		.getElementsByClass("a-link-normal s-underline-text s-underline-link-text s-link-style a-text-normal")
                        		.first().child(0).text());
                        responseDTO.setImage(ads.getElementsByTag("img").attr("src"));
                        
                        if(!StringUtils.isEmpty(ads.getElementsByClass("a-offscreen").html())
                        		&& !StringUtils.isEmpty(ads.getElementsByClass("a-price").first().getElementsByClass("a-offscreen").html())) {
                        	
                        responseDTO.setPrice((ads.getElementsByClass("a-price").first().getElementsByClass("a-offscreen").html().replaceAll("[$,]", "")));
                        }
                        responseDTO.setUrl(
                        		"https://www.amazon.com"+ads
                        		.getElementsByClass("a-link-normal s-underline-text s-underline-link-text s-link-style a-text-normal")
                        		.attr("href"));
                        responseDTO.setStore("Amazon");
                        
                    }
                    if (responseDTO.getUrl() != null && responseDTO.getPrice()!=null) {
                    	/*if(responseDTORepository.findByUrl(responseDTO.getUrl())!=null)
                        {	
                        }else
                        {responseDTO.setRequest(request);
                        }*/
                    	String s=responseDTO.getTitle();
                    	String p=responseDTO.getPrice();
                    	ResponseDTO r=responseDTORepository.findByTitle(s);
                    			
                    	if(r==null) {
                    		responseDTOS.add(responseDTO);
                    		responseDTORepository.save(responseDTO);}
                    	else if(!r.getPrice().equals(p)) {
                    		r.setPrice(responseDTO.getPrice());
                    		responseDTOS.add(r);
                    		}
                    	else
                    		responseDTOS.add(r);
                    }
                    
                }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    

}
