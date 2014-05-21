package com.hp.it.innovation.collaboration.controller.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hp.it.innovation.collaboration.builder.RoleBuilder;
import com.hp.it.innovation.collaboration.builder.TeamBuilder;
import com.hp.it.innovation.collaboration.builder.UserBuilder;
import com.hp.it.innovation.collaboration.dto.JsonResponseDTO;
import com.hp.it.innovation.collaboration.dto.RoleDTO;
import com.hp.it.innovation.collaboration.dto.TeamDTO;
import com.hp.it.innovation.collaboration.dto.UserDTO;
import com.hp.it.innovation.collaboration.dto.UserRolePrivilegeDTO;
import com.hp.it.innovation.collaboration.dto.UserTeamPrivilegeDTO;
import com.hp.it.innovation.collaboration.model.User;
import com.hp.it.innovation.collaboration.service.common.ServiceFactory;
import com.hp.it.innovation.collaboration.service.intf.RoleService;
import com.hp.it.innovation.collaboration.service.intf.TeamService;
import com.hp.it.innovation.collaboration.service.intf.UserService;

@Controller
@RequestMapping("/asyncAdmin/*")
public class AsyncAdminService {

    /*Admin role feature start*/
    private boolean addRoleValidator(RoleDTO roleDTO, String name) {
        if (roleDTO != null) {
            if (name.equals(roleDTO.getName())) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value = "/addRole", produces = "application/json")
    public @ResponseBody
    JsonResponseDTO addRole(@ModelAttribute(value = "roleDTO") RoleDTO roleDTO) {
        JsonResponseDTO res = new JsonResponseDTO();

        RoleDTO role = ServiceFactory.getService(RoleService.class).findRoleByUniqueName(roleDTO.getName());

        boolean checkRet = addRoleValidator(role, roleDTO.getName());
        if (checkRet) {
            res.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
            res.setResult("The role is already exists!!");
            return res;
        }

        ServiceFactory.getService(RoleService.class).addRole(roleDTO);
        res.setResult("Add role successfully!");
        res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
        return res;
    }

    @RequestMapping(value = "/retrieveAllRoles", produces = "application/json")
    public @ResponseBody
    List<RoleDTO> retrieveAllRoles() {
        // JsonResponseDTO res = new JsonResponseDTO();
        List<RoleDTO> roleDTOList = ServiceFactory.getService(RoleService.class).retrieveAllRoles();
        return roleDTOList;
    }
    
    @RequestMapping("/retrieveAllRoleName")
    public @ResponseBody
    JsonResponseDTO retrieveAllRoleName(){
        JsonResponseDTO res = new JsonResponseDTO();
        try {
            String roleNames = getNamesStringByNameList(getAllRoleNameList());
            res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
            res.setResult(roleNames);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
        }
        return res;
    }
    
    private List<String> getAllRoleNameList(){
        List<RoleDTO> roleDTOList = ServiceFactory.getService(RoleService.class).retrieveAllRoles();
        if(roleDTOList==null){
            return new ArrayList<String>();
        }
        return getRoleNameList(roleDTOList);
    }
    
    private List<String> getRoleNameList(List<RoleDTO> roleDTOList){
        List<String> roleNameList = new ArrayList<String>();
        for(RoleDTO roleDTO:roleDTOList){
            roleNameList.add(roleDTO.getName());
        }
        return roleNameList;
    }
    
    @RequestMapping("/getRoleInfoByRoleName")
    public @ResponseBody
    JsonResponseDTO getRoleInfoByRoleName(@ModelAttribute(value = "roleDTO") RoleDTO roleDTO){
        JsonResponseDTO res = new JsonResponseDTO();
        try {
            RoleDTO roleDTOInfo = ServiceFactory.getService(RoleService.class).findRoleByUniqueName(roleDTO.getName());
            String result = "name="+roleDTOInfo.getName()+"#roleName="+roleDTOInfo.getRoleName();
            
            List<Long> memberList = roleDTOInfo.getMembers();
            String members = getMembers(memberList);
            result += "#members="+members;
            
            String availableMembers = getAvailableMembers(memberList);
            result += "#availableMembers="+availableMembers;
            
            res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
            res.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
        }
        return res;
    }
    
    @RequestMapping("/updateRoleInfo")
    public @ResponseBody
    JsonResponseDTO updateRoleInfo(@ModelAttribute(value = "roleDTO")RoleDTO roleDTO,HttpServletRequest request){
        JsonResponseDTO res = new JsonResponseDTO();
        try {
            RoleDTO roleDTOInfo = ServiceFactory.getService(RoleService.class).findRoleByUniqueName(roleDTO.getName());
            roleDTOInfo.setRoleName(roleDTO.getRoleName());
            
            RoleBuilder roleBuilder = new RoleBuilder();
            ServiceFactory.getService(RoleService.class).save(roleBuilder.getComponent(roleDTOInfo));
            res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
        }
        return res;
    }
    
    @RequestMapping("/updateRoleMembers")
    public @ResponseBody
    JsonResponseDTO updateRoleMembers(HttpServletRequest request){
        String name = request.getParameter("name");
        String members = request.getParameter("members");
        String type = request.getParameter("type");
        JsonResponseDTO res = new JsonResponseDTO();
        try {
            RoleDTO roleDTOInfo = ServiceFactory.getService(RoleService.class).findRoleByUniqueName(name);
            List<Long> memberList = new ArrayList<Long>();
            for(String userName:members.split(",")){
                if(!"".equals(userName)){
                    UserDTO userDTO = ServiceFactory.getService(UserService.class).findUserByUniqueName(userName);
                    memberList.add(userDTO.getId());
                }
            }
            if("add".equals(type)){
                addRoleMembers(roleDTOInfo, memberList);
            }
            else if("remove".equals(type)){
                removeRoleMembers(roleDTOInfo, memberList);
            }
            res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
        }
        return res;
    }
    
    private void addRoleMembers(RoleDTO roleDTOInfo,List<Long> memberList){
        addMembersToRoleDTO(roleDTOInfo, memberList);
    }
    
    private void addMembersToRoleDTO(RoleDTO roleDTOInfo,List<Long> memberList){
        List<Long> members = roleDTOInfo.getMembers();
        for(Long member:memberList){
            Iterator<Long> it = members.iterator();
            boolean isExist = false;
            while(it.hasNext()){
                Long userId = it.next();
                if(member.equals(userId)){
                    isExist = true;
                    break;
                }
            }
            if(!isExist){
                members.add(member);
            }
        }
        RoleBuilder roleBuilder = new RoleBuilder();
        ServiceFactory.getService(RoleService.class).save(roleBuilder.getComponent(roleDTOInfo));
    }
    
    private void removeRoleMembers(RoleDTO roleDTOInfo,List<Long> memberList){
        removeMembersFromRoleDTO(roleDTOInfo, memberList);
    }
    
    private void removeMembersFromRoleDTO(RoleDTO roleDTOInfo,List<Long> memberList){
        List<Long> members = roleDTOInfo.getMembers();
        for(Long member:memberList){
            Iterator<Long> it = members.iterator();
            while(it.hasNext()){
                Long userId = it.next();
                if(member.equals(userId)){
                    it.remove();
                    break;
                }
            }
        }
        RoleBuilder roleBuilder = new RoleBuilder();
        ServiceFactory.getService(RoleService.class).save(roleBuilder.getComponent(roleDTOInfo));
    }
    /*Admin role feature end*/

    /*Admin team feature start*/
    @RequestMapping("/addTeam")
    public @ResponseBody
    JsonResponseDTO addTeam(@ModelAttribute(value = "teamDTO")TeamDTO teamDTO,HttpServletRequest request){
        String parentTeamName = request.getParameter("parentTeamName");
        JsonResponseDTO res = new JsonResponseDTO();
        TeamDTO teamDTOInfo = ServiceFactory.getService(TeamService.class).findTeamByUniqueName(teamDTO.getName());
        if(teamDTOInfo==null){
            if(parentTeamName!=null && !"".equals(parentTeamName)){
                TeamDTO parentTeamDTO = ServiceFactory.getService(TeamService.class).findTeamByUniqueName(parentTeamName);
                teamDTO.setParent(parentTeamDTO);
            }
            ServiceFactory.getService(TeamService.class).addTeam(teamDTO);
            res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
        }
        else{
            res.setResult("This team is already existing!");
            res.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
        }
        return res;
    }
    
    @RequestMapping("/retrieveAllTeams")
    public @ResponseBody
    JsonResponseDTO retrieveAllTeams(){
        JsonResponseDTO res = new JsonResponseDTO();
        try {
            String teamNames = getNamesStringByNameList(getAllTeamNameList());
            res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
            res.setResult(teamNames);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
        }
        return res;
    }
    
    private String getNamesStringByNameList(List<String> nameList){
        String names = "";
        for(String name:nameList){
            names += name+",";
        }
        if(names.length()>0){
            names = names.substring(0,names.length()-1);
        }
        return names;
    }
    
    private List<String> getTeamNameList(List<TeamDTO> teamDTOList){
        List<String> teamNameList = new ArrayList<String>();
        for(TeamDTO teamDTO:teamDTOList){
            teamNameList.add(teamDTO.getName());
        }
        return teamNameList;
    }
    
    @RequestMapping("/getTeamInfoByTeamName")
    public @ResponseBody
    JsonResponseDTO getTeamInfoByTeamName(@ModelAttribute(value = "teamDTO")TeamDTO teamDTO){
    	
    	long begin = System.currentTimeMillis(); // 测试起始时间  
        
        JsonResponseDTO res = new JsonResponseDTO();
        try {
            TeamDTO teamDTOInfo = ServiceFactory.getService(TeamService.class).findTeamByUniqueName(teamDTO.getName());
            String result = "name="+teamDTOInfo.getName()+"#teamName="+teamDTOInfo.getTeamName();
            
            List<Long> memberList = teamDTOInfo.getMembers();
            String members = getMembers(memberList);
            result += "#members="+members;
            
            String availableMembers = getAvailableMembers(memberList);
            result += "#availableMembers="+availableMembers;
            
            String availableParentTeamNames = getAvailableParentTeamNames(teamDTOInfo);
            result += "#availableParentTeamNames="+availableParentTeamNames;
            
            String parentTeamName = teamDTOInfo.getParent()==null?"":teamDTOInfo.getParent().getName();
            result += "#parentTeamName="+parentTeamName;
            
            String subTeamNames = teamDTOInfo.getSubTeams()==null?"":getNamesStringByNameList(getTeamNameList(teamDTOInfo.getSubTeams()));
            result += "#subTeamNames="+subTeamNames;
            
            res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
            res.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
        }
        
        long end = System.currentTimeMillis(); // 测试结束时间  
        System.out.println("操作所需时间：" + (end - begin) + " 毫秒"); // 打印使用时间 
        return res;
    }
    
    private String getAvailableParentTeamNames(TeamDTO teamDTOInfo){
        List<String> allTeamNameList = getAllTeamNameList();
        List<String> allChildTeamNameList = getAllChildTeamNameList(teamDTOInfo);
        allTeamNameList.removeAll(allChildTeamNameList);
        allTeamNameList.remove(teamDTOInfo.getName());
        String availableParentTeamNames = getNamesStringByNameList(allTeamNameList);
        return availableParentTeamNames;
    }
    
    private List<String> getAllTeamNameList(){
        List<TeamDTO> teamDTOList = ServiceFactory.getService(TeamService.class).retrieveAllTeams();
        if(teamDTOList==null){
            return new ArrayList<String>();
        }
        return getTeamNameList(teamDTOList);
    }
    
    private List<String> getAllChildTeamNameList(TeamDTO teamDTOInfo){
        List<String> childTeams = new ArrayList<String>();
        List<TeamDTO> subTeams = teamDTOInfo.getSubTeams();
        if(subTeams!=null){
            for(TeamDTO subTeamDTO:subTeams){
                childTeams.add(subTeamDTO.getName());
                if(subTeamDTO.getSubTeams()!=null){
                    childTeams.addAll(getAllChildTeamNameList(subTeamDTO));
                }
            }
        }
        return childTeams;
    }
    
    private String getMembers(List<Long> memberList){
        String members="";
        for(Long userId: memberList){
            User member = ServiceFactory.getService(UserService.class).fetchById(userId);
            members += member.getName()+",";
        }
        if(members.length()>0){
            members=members.substring(0,members.length()-1);
        }
        return members;
    }
    
    private String getAvailableMembers(List<Long> memberList){
        String availableMembers="";
        List<UserDTO> userDTOList = ServiceFactory.getService(UserService.class).retrieveAllUsers();
        for(UserDTO userDTO:userDTOList){
            boolean flag = false;
            for(Long userId:memberList){
                User member = ServiceFactory.getService(UserService.class).fetchById(userId);
                if(member.getName().equals(userDTO.getName())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                availableMembers+=userDTO.getName()+",";
            }
        }
        if(availableMembers.length()>0){
            availableMembers=availableMembers.substring(0,availableMembers.length()-1);
        }
        return availableMembers;
    }
    
    @RequestMapping("/updateTeamInfo")
    public @ResponseBody
    JsonResponseDTO updateTeamInfo(@ModelAttribute(value = "teamDTO")TeamDTO teamDTO,HttpServletRequest request){
        String parentTeamName = request.getParameter("parentTeamName");
        JsonResponseDTO res = new JsonResponseDTO();
        try {
            TeamDTO teamDTOInfo = ServiceFactory.getService(TeamService.class).findTeamByUniqueName(teamDTO.getName());
            teamDTOInfo.setTeamName(teamDTO.getTeamName());
            
            TeamDTO parentTeamDTO = ServiceFactory.getService(TeamService.class).findTeamByUniqueName(parentTeamName);
            teamDTOInfo.setParent(parentTeamDTO);
            
            TeamBuilder teamBuilder = new TeamBuilder();
            ServiceFactory.getService(TeamService.class).save(teamBuilder.getComponent(teamDTOInfo));
            res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
        }
        return res;
    }
    
    @RequestMapping("/updateTeamMembers")
    public @ResponseBody
    JsonResponseDTO updateTeamMembers(HttpServletRequest request){
        String name = request.getParameter("name");
        String members = request.getParameter("members");
        String type = request.getParameter("type");
        JsonResponseDTO res = new JsonResponseDTO();
        try {
            TeamDTO teamDTOInfo = ServiceFactory.getService(TeamService.class).findTeamByUniqueName(name);
            List<Long> memberList = new ArrayList<Long>();
            for(String userName:members.split(",")){
                if(!"".equals(userName)){
                    UserDTO userDTO = ServiceFactory.getService(UserService.class).findUserByUniqueName(userName);
                    memberList.add(userDTO.getId());
                }
            }
            if("add".equals(type)){
                addMembers(teamDTOInfo, memberList);
            }
            else if("remove".equals(type)){
                removeMembers(teamDTOInfo, memberList);
            }
            res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
        }
        return res;
    }
    
    private void addMembers(TeamDTO teamDTOInfo,List<Long> memberList){
        addMembersToTeamDTO(teamDTOInfo, memberList);
        addMembersToParentTeam(teamDTOInfo, memberList);
    }
    
    private void addMembersToTeamDTO(TeamDTO teamDTOInfo,List<Long> memberList){
        List<Long> members = teamDTOInfo.getMembers();
        for(Long member:memberList){
            Iterator<Long> it = members.iterator();
            boolean isExist = false;
            while(it.hasNext()){
                Long userId = it.next();
                if(member.equals(userId)){
                    isExist = true;
                    break;
                }
            }
            if(!isExist){
                members.add(member);
            }
        }
        TeamBuilder teamBuilder = new TeamBuilder();
        ServiceFactory.getService(TeamService.class).save(teamBuilder.getComponent(teamDTOInfo));
    }
    
    private void addMembersToParentTeam(TeamDTO teamDTOInfo,List<Long> memberList){
        if(teamDTOInfo.getParent()!=null){
            TeamDTO parentDTO = teamDTOInfo.getParent();
            addMembersToTeamDTO(parentDTO, memberList);
            addMembersToParentTeam(parentDTO, memberList);
        }
    }
    
    private void removeMembers(TeamDTO teamDTOInfo,List<Long> memberList){
        removeMembersFromTeamDTO(teamDTOInfo, memberList);
        removeMembersFromChildTeam(teamDTOInfo, memberList);
    }
    
    private void removeMembersFromTeamDTO(TeamDTO teamDTOInfo,List<Long> memberList){
        List<Long> members = teamDTOInfo.getMembers();
        for(Long member:memberList){
            Iterator<Long> it = members.iterator();
            while(it.hasNext()){
                Long userId = it.next();
                if(member.equals(userId)){
                    it.remove();
                    break;
                }
            }
        }
        TeamBuilder teamBuilder = new TeamBuilder();
        ServiceFactory.getService(TeamService.class).save(teamBuilder.getComponent(teamDTOInfo));
    }
    
    private void removeMembersFromChildTeam(TeamDTO teamDTOInfo,List<Long> memberList){
        if(teamDTOInfo.getSubTeams()!=null){
            List<TeamDTO> subTeamList = teamDTOInfo.getSubTeams();
            for(TeamDTO subTeam : subTeamList){
                removeMembersFromTeamDTO(subTeam, memberList);
                removeMembersFromChildTeam(subTeam, memberList);
            }
        }
    }
    /*Admin team feature end*/
    
    
    @RequestMapping(value="/retrieveTeamsPrivilege")
    public @ResponseBody
    UserTeamPrivilegeDTO retrieveTeamsPrivilege(@ModelAttribute(value = "userDTO")UserDTO u){
    	try{
    		System.out.println("retrieveTeamsPrivilege...");
        	UserTeamPrivilegeDTO res = new UserTeamPrivilegeDTO();
        	UserDTO userDTO = ServiceFactory.getService(UserService.class).findUserByUniqueName(u.getName());
        	
        	Map<String,String> allTeams = new HashMap<String,String>();
        	Map<String,String> selectedTeams = new HashMap<String,String>();
        	Map<String,String> availableTeams = new HashMap<String,String>();
        	List<TeamDTO> teamDTOList = ServiceFactory.getService(TeamService.class).retrieveAllTeams();
        	
        	for(TeamDTO team : teamDTOList){
        		if(!StringUtils.isEmpty(team.getName()) && !StringUtils.isEmpty(team.getTeamName())){
        			allTeams.put(team.getName(), team.getTeamName());
        		}
        	}
        	
        	if(userDTO.getTeams()!=null){
        		for(TeamDTO team: userDTO.getTeams()){
            		if(!StringUtils.isEmpty(team.getName()) && !StringUtils.isEmpty(team.getTeamName())){
            			selectedTeams.put(team.getName(), team.getTeamName());
            		}
            	}
        	}
        	
        	for(String s : allTeams.keySet()){
        		if(!selectedTeams.containsKey(s)){
        			availableTeams.put(s, allTeams.get(s));
        		}
        	}
        	
        	System.out.println("availableTeams"+availableTeams);
        	System.out.println("selectedTeams"+selectedTeams);
        	
        	res.getAvailableTeams().putAll(availableTeams);
        	res.getSelectedTeams().putAll(selectedTeams);
            return res;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
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

    
    @RequestMapping(value="/saveTeamsPrivilege")
    public @ResponseBody
    JsonResponseDTO saveTeamsPrivilege(HttpServletRequest request, @ModelAttribute(value = "userDTO")UserDTO u){
    	try{
    		System.out.println("saveTeamsPrivilege...");
        	JsonResponseDTO res = new JsonResponseDTO();
            String selectedOpt = request.getParameter("selectedOpt");
            System.out.println(selectedOpt);
            
            UserDTO userDTO = ServiceFactory.getService(UserService.class).findUserByUniqueName(u.getName());
    		
        	Set<TeamDTO> set = new HashSet<TeamDTO>();
            
    		String[] selectOpts = selectedOpt.split(",");
    		for (int i = 0; i < selectOpts.length; i++) {
    			String name = selectOpts[i];
    			if(!StringUtils.isEmpty(name)){
    				TeamDTO teamDTO = ServiceFactory.getService(TeamService.class).findTeamByUniqueName(name);
    				set.add(teamDTO);
    				recursion(set, teamDTO);
    			}
    		}
    		
    		List<TeamDTO> list = new ArrayList<TeamDTO>();
    		list.addAll(set);
    		userDTO.setTeams(list);
    		
    		System.out.println("hp-----------");
    		for(TeamDTO t : list){
    			System.out.println(t.getName());
    		}
    		UserBuilder userBuilder = new UserBuilder();
    		ServiceFactory.getService(UserService.class).merge(userBuilder.getComponent(userDTO));
    		res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
    		
            return res;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
    
    @RequestMapping("/retrieveAllUsers")
    public @ResponseBody
    JsonResponseDTO retrieveAllUsers(){
        JsonResponseDTO res = new JsonResponseDTO();
        List<UserDTO> userDTOList = ServiceFactory.getService(UserService.class).retrieveAllUsers();
        String users = "";
        System.out.println("All teams info:");
        System.out.println("name\tdisplayName");
        for(UserDTO userDTO : userDTOList){
        	users += userDTO.getName()+"#";
        }
        System.out.println("users value:"+users);
        res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
        res.setResult(users);
        return res;
    }
    
    @RequestMapping(value="/retrieveRolesPrivilege")
    public @ResponseBody
    UserRolePrivilegeDTO retrieveRolesPrivilege(@ModelAttribute(value = "userDTO")UserDTO u){
    	try{
    		System.out.println("retrieveRolesPrivilege...");
    		UserRolePrivilegeDTO res = new UserRolePrivilegeDTO();
        	UserDTO userDTO = ServiceFactory.getService(UserService.class).findUserByUniqueName(u.getName());
        	
        	Map<String,String> allRoles = new HashMap<String,String>();
        	Map<String,String> selectedRoles = new HashMap<String,String>();
        	Map<String,String> availableRoles = new HashMap<String,String>();
        	List<RoleDTO> roleDTOList = ServiceFactory.getService(RoleService.class).retrieveAllRoles();
        	
        	for(RoleDTO role : roleDTOList){ 
        		if(!StringUtils.isEmpty(role.getName()) && !StringUtils.isEmpty(role.getRoleName())){
        			allRoles.put(role.getName(), role.getRoleName());
        		}
        	}
        	
        	if(userDTO.getRoles()!=null){
        		for(RoleDTO team: userDTO.getRoles()){
            		if(!StringUtils.isEmpty(team.getName()) && !StringUtils.isEmpty(team.getRoleName())){
            			selectedRoles.put(team.getName(), team.getRoleName());
            		}
            	}
        	}
        	
        	for(String s : allRoles.keySet()){
        		if(!selectedRoles.containsKey(s)){
        			availableRoles.put(s, allRoles.get(s));
        		}
        	}
        	
        	res.getAvailableRoles().putAll(availableRoles);
        	res.getSelectedRoles().putAll(selectedRoles);
            return res;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    	
    }
    
    
    @RequestMapping(value="/saveRolesPrivilege")
    public @ResponseBody
    JsonResponseDTO saveRolesPrivilege(HttpServletRequest request, @ModelAttribute(value = "userDTO")UserDTO u){
    	try{
    		System.out.println("saveRolesPrivilege...");
        	JsonResponseDTO res = new JsonResponseDTO();
            String selectedOpt = request.getParameter("selectedOpt2");
            System.out.println(selectedOpt);
            
            UserDTO userDTO = ServiceFactory.getService(UserService.class).findUserByUniqueName(u.getName());
    		
        	List<RoleDTO> list = new ArrayList<RoleDTO>();
            
    		String[] selectOpts = selectedOpt.split(",");
    		for (int i = 0; i < selectOpts.length; i++) {
    			String name = selectOpts[i];
    			if(!StringUtils.isEmpty(name)){
    				RoleDTO roleDTO = ServiceFactory.getService(RoleService.class).findRoleByUniqueName(name);
    				list.add(roleDTO);
    			}
    		}

    		userDTO.setRoles(list);
    		UserBuilder userBuilder = new UserBuilder();
    		ServiceFactory.getService(UserService.class).merge(userBuilder.getComponent(userDTO));
    		res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
    		
            return res;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
    
    
    @RequestMapping(value="/retrieveUserStatus")
    public @ResponseBody
    JsonResponseDTO retrieveUserStatus(@ModelAttribute(value = "userDTO")UserDTO u){
    	try{
    		System.out.println("retrieveUserStatus...");
    		JsonResponseDTO res = new JsonResponseDTO();
        	UserDTO userDTO = ServiceFactory.getService(UserService.class).findUserByUniqueName(u.getName());
        	
        	String status = userDTO.getStatus();
        	
        	if("1".equals(status)){
        		res.setResult("1");
        	}else{
        		res.setResult("0");
        	}
            return res;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    	
    }
    
    @RequestMapping(value="/saveUserStatus")
    public @ResponseBody
    JsonResponseDTO saveUserStatus(HttpServletRequest request, @ModelAttribute(value = "userDTO")UserDTO u){
    	try{
    		System.out.println("saveUserStatus...");
        	JsonResponseDTO res = new JsonResponseDTO();
            String status = request.getParameter("status");
            
            UserDTO userDTO = ServiceFactory.getService(UserService.class).findUserByUniqueName(u.getName());
    		
            userDTO.setStatus(status);

    		UserBuilder userBuilder = new UserBuilder();
    		ServiceFactory.getService(UserService.class).merge(userBuilder.getComponent(userDTO));
    		res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
    		
            return res;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
}

