package com.example.bookstore;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponse;
import com.example.bookstore.service.BookCatalogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//what is that, search this annotation?
@SpringBootTest(
		classes=BookstoreSpringDataApplication.class,
        webEnvironment=WebEnvironment.MOCK)

@AutoConfigureMockMvc
class BookstoreSpringDataApplicationTests {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ModelMapper modelMapper;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Before(value = "")
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	@MockBean
	BookCatalogService bookCatalogService;
	
//	@DisplayName("something")
	@ParameterizedTest
	@CsvFileSource(resources="bookcatalog.csv")
	void addBookShouldReturnOk(
			String isbn,
			String author,
	        String title,
			int pages,
			int year,
			double price,
			String cover
			) throws Throwable  {
		var request=new BookRequest();
		request.setIsbn(isbn);
		request.setAuthor(author);
		request.setTitle(title);
		request.setPages(pages);
		request.setYear(year);
		request.setPrice(price);
		request.setCover(cover);
		
		var response=modelMapper.map(request, BookResponse.class);
		Mockito.when(bookCatalogService.addBook(request))
		.thenReturn(response);
		
		mockMvc.perform(
				post("/books")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				)
		// 3. Verification
				.andExpect(status().isOk())
				.andDo(print())
				//.andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.isbn",is(isbn.getBytes())))
		        .andExpect(jsonPath("$.author",is(author)))
		        .andExpect(jsonPath("$.title",is(title)))
		        .andExpect(jsonPath("$.pages",is(pages)))
		        .andExpect(jsonPath("$.year",is(year)))
		        .andExpect(jsonPath("$.price",is(price)))
		        .andExpect(jsonPath("$.cover",is(cover)));
				// 4. Tear-down
		
		
	}
	
	
	@ParameterizedTest
	@CsvFileSource(resources = "bookcatalog.csv")
	void getBooksByIsbnShouldReturnOk(
		String isbn,
		String author,
        String title,
		int pages,
		int year,
		double price,
		String cover
		) throws Throwable  {
	/*var request=new BookRequest();
	request.setIsbn(isbn);
	request.setAuthor(author);
	request.setTitle(title);
	request.setPages(pages);
	request.setYear(year);
	request.setPrice(price);
	request.setCover(cover);
	*/
	var response=new BookResponse();
	response.setIsbn(isbn);
	response.setAuthor(author);
	response.setTitle(title);
	response.setPages(pages);
	response.setYear(year);
	response.setPrice(price);
	response.setCover(cover);
	
	Mockito.when(bookCatalogService.findBookByIsbn(isbn))
	.thenReturn(response);
	
	  mockMvc.perform(
	        	get("/books/"+isbn)
	            .accept(MediaType.APPLICATION_JSON)
	        )
	  .andExpect(status().isOk())
	  .andExpect(jsonPath("$.isbn",is(isbn)))
      .andExpect(jsonPath("$.author",is(author)))
      .andExpect(jsonPath("$.title",is(title)))
      .andExpect(jsonPath("$.pages",is(pages)))
      .andExpect(jsonPath("$.year",is(year)))
      .andExpect(jsonPath("$.price",is(price)))
      .andExpect(jsonPath("$.cover",is(cover)));
	
	}
	
	
	@ParameterizedTest
	@CsvFileSource(resources = "bookcatalog.csv")
	void removeCustomerByIdentityShoudlReturnOk(
			String isbn,
			String author,
	        String title,
			int pages,
			int year,
			double price,
			String cover
			) throws Throwable  {
	
		var response=new BookResponse();
		response.setIsbn(isbn);
		response.setAuthor(author);
		response.setTitle(title);
		response.setPages(pages);
		response.setYear(year);
		response.setPrice(price);
		response.setCover(cover);
		Mockito.when(bookCatalogService.deleteBook(isbn))
		       .thenReturn(response);
		// 2. Call exercise method
        mockMvc.perform(
        	delete("/customers/"+identity).accept(MediaType.APPLICATION_JSON)
        )
        // 3. Verification
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.identity",is(identity)))
        .andExpect(jsonPath("$.fullname",is(fullname)))
        .andExpect(jsonPath("$.email",is(email)))
        .andExpect(jsonPath("$.phone",is(phone)));
		// 4. Tear-down
	}
	
	
	
}
