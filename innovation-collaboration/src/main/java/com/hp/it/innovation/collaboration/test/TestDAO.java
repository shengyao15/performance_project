package com.hp.it.innovation.collaboration.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.hp.it.innovation.collaboration.builder.UserBuilder;
import com.hp.it.innovation.collaboration.dto.JsonResponseDTO;
import com.hp.it.innovation.collaboration.dto.TeamDTO;
import com.hp.it.innovation.collaboration.dto.UserDTO;
import com.hp.it.innovation.collaboration.model.User;
import com.hp.it.innovation.collaboration.service.common.ServiceFactory;
import com.hp.it.innovation.collaboration.service.intf.TeamService;
import com.hp.it.innovation.collaboration.service.intf.UserService;

public class TestDAO extends TestCase {

	@SuppressWarnings("unused")
	public void testF() {
		try{
			UserDTO userDTO = ServiceFactory.getService(UserService.class).findUserByUniqueName("tristan1@hp.com");
        	
			List<TeamDTO> AllList = ServiceFactory.getService(TeamService.class).retrieveAllTeams();
			Set<TeamDTO> list = new HashSet();
    		TeamDTO teamDTO = ServiceFactory.getService(TeamService.class).findTeamByUniqueName("jie");
    		TeamDTO teamDTO2 = ServiceFactory.getService(TeamService.class).findTeamByUniqueName("lishi");
    		list.add(teamDTO);
    		list.add(teamDTO2);
    		
    		recursion(list, teamDTO);
    		
    		for(TeamDTO t : list){
    			System.out.println(t.getName());
    		}
    		
    		List<TeamDTO> list2 = new ArrayList<TeamDTO>();
    		
    		list2.addAll(list);
    		userDTO.setTeams(list2);
    		UserBuilder userBuilder = new UserBuilder();
    		User user = userBuilder.getComponent(userDTO);
    		ServiceFactory.getService(UserService.class).merge(user);
    		
    		System.out.println("end");
    	}catch(Throwable e){
    		e.printStackTrace();
    	}
	}
	
	public void recursion(Set<TeamDTO> list, TeamDTO teamDTO){
		if(teamDTO.getParent()!=null){
			System.out.println("1--");
			list.add(teamDTO.getParent());
			recursion(list, teamDTO.getParent());
		}else{
			System.out.println("2--");
		}
	}

	
	
	public void xtestE() {
		try{
    		System.out.println("saveTeamsPrivilege...");
            
            UserDTO userDTO = ServiceFactory.getService(UserService.class).findUserByUniqueName("tristan1@hp.com");
    		
        	List<TeamDTO> list = new ArrayList<TeamDTO>();
            
    		TeamDTO teamDTO = ServiceFactory.getService(TeamService.class).findTeamByUniqueName("jie");

    		list.add(teamDTO);
    		
    		userDTO.setTeams(list);
    		UserBuilder userBuilder = new UserBuilder();
    		User user = userBuilder.getComponent(userDTO);
    		ServiceFactory.getService(UserService.class).merge(user);
    		
    	}catch(Throwable e){
    		e.printStackTrace();
    	}
	}
	
	
	public void xtestA() {
		List<UserDTO> users = ServiceFactory.getService(UserService.class).retrieveAllUsers();
		List<User> users2 = ServiceFactory.getService(UserService.class).findAll();
		System.out.println(users2);
	}

	public void xtestB() {
		UserDTO user = ServiceFactory.getService(UserService.class).findUserByUniqueName("t@hp.com");
	}
	
	public void xtestC(){
		
		// TeamName = a,b,c,
		UserDTO user = ServiceFactory.getService(UserService.class).findUserByUniqueName("t@hp.com");
		
		user.getTeams().clear();
		TeamDTO team = new TeamDTO();
		
	}
	
	public void xtestD(){
		
		// TeamName = a,b,c,
		UserDTO user = ServiceFactory.getService(UserService.class).findUserByUniqueName("t@hp.com");
		List<TeamDTO> list = new ArrayList<TeamDTO>();
		
		//String s ="test2,aa,f,xx,aaa,";
		String s = "sopher,jie,test1,test2,aa,f,xx,aaa,abc,eee,eee2,new team1,tt,";
		String[] selectOpts = s.split(",");
		for (int i = 0; i < selectOpts.length; i++) {
			String name = selectOpts[i];
			if(!StringUtils.isEmpty(name)){
				TeamDTO teamDTO = ServiceFactory.getService(TeamService.class).findTeamByUniqueName(name);
				list.add(teamDTO);
			}
		}
		user.setTeams(list);
		UserBuilder userBuilder = new UserBuilder();
		ServiceFactory.getService(UserService.class).save(userBuilder.getComponent(user));
	}
}
