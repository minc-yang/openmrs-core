package org.openmrs.api.db.hibernate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptProposal;
import org.openmrs.ConceptSet;
import org.openmrs.ConceptSetDerived;
import org.openmrs.ConceptWord;
import org.openmrs.DataEntryStatistic;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.MimeType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.Privilege;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.Role;
import org.openmrs.Tribe;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.AdministrationDAO;
import org.openmrs.api.db.DAOException;
import org.openmrs.reporting.AbstractReportObject;
import org.openmrs.reporting.Report;
import org.openmrs.reporting.ReportObjectWrapper;
import org.openmrs.util.OpenmrsConstants;

public class HibernateAdministrationDAO implements
		AdministrationDAO {

	protected Log log = LogFactory.getLog(getClass());

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	public HibernateAdministrationDAO() { }
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) { 
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationDAO#createPerson(org.openmrs.Person)
	 */
	public void createPerson(Person person) throws DAOException {
		sessionFactory.getCurrentSession().save(person);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationDAO#deletePerson(org.openmrs.Person)
	 */
	public void deletePerson(Person person) throws DAOException {
		sessionFactory.getCurrentSession().delete(person);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationDAO#getPerson(java.lang.Integer)
	 */
	public Person getPerson(Integer personId) throws DAOException {
		return (Person)sessionFactory.getCurrentSession().get(Person.class, personId);
	}
	
	public Person getPerson(Patient pat) throws DAOException {
		/*
		Criteria crit = session.createCriteria(Person.class);
		crit = crit.createAlias("patient", "p");
		crit = crit.add(Expression.eq("p.patientId", pat.getPatientId()));
		Object o = crit.uniqueResult();
		*/
		
		
		Query query = sessionFactory.getCurrentSession().createQuery("from Person p where p.patient.patientId = :patId");
		
		if (pat == null || pat.getPatientId() == null)
			return null;
		query = query.setInteger("patId", pat.getPatientId());
		Object o = query.uniqueResult();
		
		log.debug("o.class: " + o.getClass().toString());
		
		Person person = (Person)o;
		
		return person;
	}
	
	
	public Person getPerson(User user) throws DAOException {
		Query query = sessionFactory.getCurrentSession().createQuery("from Person p where p.user.userId = :userId");
		query = query.setInteger("userId", user.getUserId());
		Object o = query.uniqueResult();
		
		log.debug("o.class: " + o.getClass().toString());
		
		Person person = (Person)o;
		
		return person;
	}


	/**
	 * @see org.openmrs.api.db.AdministrationDAO#updatePerson(org.openmrs.Person)
	 */
	public void updatePerson(Person person) throws DAOException {
		if (person.getPersonId() == null)
			createPerson(person);
		else {
			sessionFactory.getCurrentSession().saveOrUpdate(person);
		}
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#createEncounterType(org.openmrs.EncounterType)
	 */
	public void createEncounterType(EncounterType encounterType) throws DAOException {
		sessionFactory.getCurrentSession().save(encounterType);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#updateEncounterType(org.openmrs.EncounterType)
	 */
	public void updateEncounterType(EncounterType encounterType) throws DAOException {
		if (encounterType.getEncounterTypeId() == null)
			createEncounterType(encounterType);
		else
			sessionFactory.getCurrentSession().saveOrUpdate(encounterType);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteEncounterType(org.openmrs.EncounterType)
	 */
	public void deleteEncounterType(EncounterType encounterType) throws DAOException {
		sessionFactory.getCurrentSession().delete(encounterType);
	}

	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createFieldType(org.openmrs.FieldType)
	 */
	public void createFieldType(FieldType fieldType) throws DAOException {
		fieldType.setCreator(Context.getAuthenticatedUser());
		fieldType.setDateCreated(new Date());
		sessionFactory.getCurrentSession().save(fieldType);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteFieldType(org.openmrs.FieldType)
	 */
	public void deleteFieldType(FieldType fieldType) throws DAOException {
		sessionFactory.getCurrentSession().delete(fieldType);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#updateFieldType(org.openmrs.FieldType)
	 */
	public void updateFieldType(FieldType fieldType) throws DAOException {
		if (fieldType.getFieldTypeId() == null)
			createFieldType(fieldType);
		else
			sessionFactory.getCurrentSession().saveOrUpdate(fieldType);
	}

	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createLocation(org.openmrs.Location)
	 */
	public void createLocation(Location location) throws DAOException {
		location.setCreator(Context.getAuthenticatedUser());
		location.setDateCreated(new Date());
		sessionFactory.getCurrentSession().save(location);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#updateLocation(org.openmrs.Location)
	 */
	public void updateLocation(Location location) throws DAOException {
		if (location.getLocationId() == null)
			createLocation(location);
		else
			sessionFactory.getCurrentSession().saveOrUpdate(location);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteLocation(org.openmrs.Location)
	 */
	public void deleteLocation(Location location) throws DAOException {
		sessionFactory.getCurrentSession().delete(location);
	}

	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createMimeType(org.openmrs.MimeType)
	 */
	public void createMimeType(MimeType mimeType) throws DAOException {
		sessionFactory.getCurrentSession().save(mimeType);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#updateMimeType(org.openmrs.MimeType)
	 */
	public void updateMimeType(MimeType mimeType) throws DAOException {
		if (mimeType.getMimeTypeId() == null)
			createMimeType(mimeType);
		else
			sessionFactory.getCurrentSession().saveOrUpdate(mimeType);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteMimeType(org.openmrs.MimeType)
	 */
	public void deleteMimeType(MimeType mimeType) throws DAOException {
		sessionFactory.getCurrentSession().delete(mimeType);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createPatientIdentifierType(org.openmrs.PatientIdentifierType)
	 */
	public void createPatientIdentifierType(PatientIdentifierType patientIdentifierType) throws DAOException {
		patientIdentifierType.setCreator(Context.getAuthenticatedUser());
		patientIdentifierType.setDateCreated(new Date());
		sessionFactory.getCurrentSession().save(patientIdentifierType);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#updatePatientIdentifierType(org.openmrs.PatientIdentifierType)
	 */
	public void updatePatientIdentifierType(PatientIdentifierType patientIdentifierType) throws DAOException {
		if (patientIdentifierType.getPatientIdentifierTypeId() == null)
			createPatientIdentifierType(patientIdentifierType);
		else
			sessionFactory.getCurrentSession().saveOrUpdate(patientIdentifierType);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#deletePatientIdentifierType(org.openmrs.PatientIdentifierType)
	 */
	public void deletePatientIdentifierType(PatientIdentifierType patientIdentifierType) throws DAOException {
		sessionFactory.getCurrentSession().delete(patientIdentifierType);
	}

	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createRelationshipType(org.openmrs.RelationshipType)
	 */
	public void createRelationshipType(RelationshipType relationshipType) throws DAOException {
		relationshipType.setCreator(Context.getAuthenticatedUser());
		relationshipType.setDateCreated(new Date());
		sessionFactory.getCurrentSession().save(relationshipType);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#updateRelationshipType(org.openmrs.RelationshipType)
	 */
	public void updateRelationshipType(RelationshipType relationshipType) throws DAOException {
		if (relationshipType.getRelationshipTypeId() == null)
			createRelationshipType(relationshipType);
		else
			sessionFactory.getCurrentSession().saveOrUpdate(relationshipType);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteRelationshipType(org.openmrs.RelationshipType)
	 */
	public void deleteRelationshipType(RelationshipType relationshipType) throws DAOException {
		sessionFactory.getCurrentSession().delete(relationshipType);
	}

	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createTribe(org.openmrs.Tribe)
	 */
	public void createTribe(Tribe tribe) throws DAOException {
		sessionFactory.getCurrentSession().save(tribe);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#updateTribe(org.openmrs.Tribe)
	 */
	public void updateTribe(Tribe tribe) throws DAOException {
		if (tribe.getTribeId() == null)
			createTribe(tribe);
		else
			sessionFactory.getCurrentSession().saveOrUpdate(tribe);
	}	

	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteTribe(org.openmrs.Tribe)
	 */
	public void deleteTribe(Tribe tribe) throws DAOException {
		sessionFactory.getCurrentSession().delete(tribe);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#retireTribe(org.openmrs.Tribe)
	 */
	public void retireTribe(Tribe tribe) throws DAOException {
		tribe.setRetired(true);
		updateTribe(tribe);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#unretireTribe(org.openmrs.Tribe)
	 */
	public void unretireTribe(Tribe tribe) throws DAOException {
		tribe.setRetired(false);
		updateTribe(tribe);
	}

	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createRelationship(org.openmrs.Relationship)
	 */
	public void createRelationship(Relationship relationship) throws DAOException {
		relationship.setCreator(Context.getAuthenticatedUser());
		relationship.setDateCreated(new Date());
		sessionFactory.getCurrentSession().save(relationship);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#updateRelationship(org.openmrs.Relationship)
	 */
	public void updateRelationship(Relationship relationship) throws DAOException {
		if (relationship.getRelationshipId() == null)
			createRelationship(relationship);
		else
			sessionFactory.getCurrentSession().saveOrUpdate(relationship);
	}	

	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteRelationship(org.openmrs.Relationship)
	 */
	public void deleteRelationship(Relationship relationship) throws DAOException {
		sessionFactory.getCurrentSession().delete(relationship);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#voidRelationship(org.openmrs.Relationship)
	 */
	public void voidRelationship(Relationship relationship) throws DAOException {
		relationship.setVoided(true);
		updateRelationship(relationship);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#unvoidRelationship(org.openmrs.Relationship)
	 */
	public void unvoidRelationship(Relationship relationship) throws DAOException {
		relationship.setVoided(false);
		relationship.setVoidedBy(null);
		relationship.setDateVoided(null);
		relationship.setVoidReason(null);
		updateRelationship(relationship);
	}

	
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createRole(org.openmrs.Role)
	 */
	public void createRole(Role role) throws DAOException {
		sessionFactory.getCurrentSession().save(role);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteRole(org.openmrs.Role)
	 */
	public void deleteRole(Role role) throws DAOException {
		sessionFactory.getCurrentSession().delete(role);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#updateRole(org.openmrs.Role)
	 */
	public void updateRole(Role role) throws DAOException {
		if (role.getRole() == null)
			createRole(role);
		else {
			sessionFactory.getCurrentSession().merge(role);
		}
	}	
	
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createPrivilege(org.openmrs.Privilege)
	 */
	public void createPrivilege(Privilege privilege) throws DAOException {
		sessionFactory.getCurrentSession().save(privilege);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#updatePrivilege(org.openmrs.Privilege)
	 */
	public void updatePrivilege(Privilege privilege) throws DAOException {
		if (privilege.getPrivilege() == null)
			createPrivilege(privilege);
		else {
			sessionFactory.getCurrentSession().saveOrUpdate(privilege);
		}
	}	

	/**
	 * @see org.openmrs.api.db.AdministrationService#deletePrivilege(org.openmrs.Privilege)
	 */
	public void deletePrivilege(Privilege privilege) throws DAOException {
		sessionFactory.getCurrentSession().delete(privilege);
	}
	
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createConceptClass(org.openmrs.ConceptClass)
	 */
	public void createConceptClass(ConceptClass cc) throws DAOException {
		cc.setCreator(Context.getAuthenticatedUser());
		cc.setDateCreated(new Date());
		sessionFactory.getCurrentSession().save(cc);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#updateConceptClass(org.openmrs.ConceptClass)
	 */
	public void updateConceptClass(ConceptClass cc) throws DAOException {
		if (cc.getConceptClassId() == null)
			createConceptClass(cc);
		else {
			sessionFactory.getCurrentSession().saveOrUpdate(cc);
		}
	}	
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteConceptClass(org.openmrs.ConceptClass)
	 */
	public void deleteConceptClass(ConceptClass cc) throws DAOException {
		sessionFactory.getCurrentSession().delete(cc);
	}

	
	/**
	 * @see org.openmrs.api.db.AdministrationService#createConceptClass(org.openmrs.ConceptClass)
	 */
	public void createConceptDatatype(ConceptDatatype cd) throws DAOException {
		cd.setCreator(Context.getAuthenticatedUser());
		cd.setDateCreated(new Date());
		sessionFactory.getCurrentSession().save(cd);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#updateConceptDatatype(org.openmrs.ConceptDatatype)
	 */
	public void updateConceptDatatype(ConceptDatatype cd) throws DAOException {
		if (cd.getConceptDatatypeId() == null)
			createConceptDatatype(cd);
		else {
			sessionFactory.getCurrentSession().saveOrUpdate(cd);
		}
	}	
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteConceptDatatype(org.openmrs.ConceptDatatype)
	 */
	public void deleteConceptDatatype(ConceptDatatype cd) throws DAOException {
		sessionFactory.getCurrentSession().delete(cd);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#createReport(org.openmrs.reporting.Report)
	 */
	public void createReport(Report r) throws DAOException {
		r.setCreator(Context.getAuthenticatedUser());
		r.setDateCreated(new Date());
		sessionFactory.getCurrentSession().save(r);
	}

	/**
	 * @see org.openmrs.api.db.AdministrationService#updateReport(org.openmrs.reporting.Report)
	 */
	public void updateReport(Report r) throws DAOException {
		if (r.getReportId() == null)
			createReport(r);
		else {
			sessionFactory.getCurrentSession().saveOrUpdate(r);
		}
	}	
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#deleteReport(org.openmrs.reporting.Report)
	 */
	public void deleteReport(Report r) throws DAOException {
		sessionFactory.getCurrentSession().delete(r);
	}
	
	public void updateConceptWord(Concept concept) throws DAOException {
		if (concept != null) {
			// remove all old words
			deleteConceptWord(concept);
			
			// add all new words
			Collection<ConceptWord> words = ConceptWord.makeConceptWords(concept);
			log.debug("words: " + words);
			for (ConceptWord word : words) {
				try {
					sessionFactory.getCurrentSession().save(word);
				}
				catch (NonUniqueObjectException e) {
					ConceptWord tmp  = (ConceptWord)sessionFactory.getCurrentSession().merge(word);
					sessionFactory.getCurrentSession().evict(tmp);
					sessionFactory.getCurrentSession().save(word);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
    public void deleteConceptWord(Concept concept) throws DAOException {
		if (concept != null) {
			Criteria crit = sessionFactory.getCurrentSession().createCriteria(ConceptWord.class);
			crit.add(Expression.eq("concept", concept));
			
			List<ConceptWord> words = crit.list();
			
			Integer authUserId = null;
			if (Context.isAuthenticated())
				authUserId = Context.getAuthenticatedUser().getUserId();
			
			log.debug(authUserId + "|ConceptWord|" + words);
			
			sessionFactory.getCurrentSession().createQuery("delete from ConceptWord where concept_id = :c")
					.setInteger("c", concept.getConceptId())
					.executeUpdate();
		}
	}
	
	public void updateConceptSetDerived(Concept concept) throws DAOException {
		log.debug("Updating concept set derivisions for #" + concept.getConceptId().toString());
		
		// deletes current concept's sets and matching parent's sets

		//recursively get all parents
		List<Concept> parents = getParents(concept);
		
		// delete this concept's children and their bursted parents
		for (Concept parent : parents) {
			sessionFactory.getCurrentSession().createQuery("delete from ConceptSetDerived csd where csd.concept in (select cs.concept from ConceptSet cs where cs.conceptSet = :c) and csd.conceptSet = :parent)")
					.setParameter("c", concept)
					.setParameter("parent", parent)
					.executeUpdate();
		}
		
		//set of updates to be passed to the server (unique list)
		Set<ConceptSetDerived> updates = new HashSet<ConceptSetDerived>();
		
		//add parents as sets of parents below
		ConceptSetDerived csd;
		for (Integer a = 0; a < parents.size() - 1; a++) {
			Concept set = parents.get(a);
			for (Integer b = a + 1; b < parents.size(); b++) {
				Concept conc = parents.get(b);
				csd = new ConceptSetDerived(set, conc, Double.valueOf(b.doubleValue()));
				updates.add(csd);
			}
		}
		
		//recursively add parents to children
		updates.addAll(deriveChildren(parents, concept));
		
		for (ConceptSetDerived c : updates) {
			sessionFactory.getCurrentSession().saveOrUpdate(c);
		}

	}
	
	private Set<ConceptSetDerived> deriveChildren(List<Concept> parents, Concept current) {
		Set<ConceptSetDerived> updates = new HashSet<ConceptSetDerived>();
		
		ConceptSetDerived derivedSet = null;
		// make each child a direct child of each parent/grandparent
		for (ConceptSet childSet : current.getConceptSets()) {
			Concept child = childSet.getConcept();
			log.debug("Deriving child: " + child.getConceptId());
			Double sort_weight = childSet.getSortWeight();
			for (Concept parent : parents) {
				log.debug("Matching child: " + child.getConceptId() + " with parent: " + parent.getConceptId());
				derivedSet = new ConceptSetDerived(parent, child, sort_weight++);
				updates.add(derivedSet);
			}
			
			//recurse if this child is a set as well
			if (child.isSet()) {
				log.debug("Concept id: " + child.getConceptId() + " is a set");
				List<Concept> new_parents = new Vector<Concept>();
				new_parents.addAll(parents);
				new_parents.add(child);
				updates.addAll(deriveChildren(new_parents, child));
			}
		}
		
		return updates;
	}
	
	
	@SuppressWarnings("unchecked")
    private List<Concept> getParents(Concept current) {
		List<Concept> parents = new Vector<Concept>();
		
		if (current != null) {
			
			Query query = sessionFactory.getCurrentSession().createQuery("from Concept c join c.conceptSets sets where sets.concept = ?")
									.setEntity(0, current);
			List<Concept> immed_parents = query.list();
			
			for (Concept c : immed_parents) {
				parents.addAll(getParents(c));
			}
			
			parents.add(current);
			
			if (log.isDebugEnabled()) {
				log.debug("parents found: ");
				for (Concept c : parents) {
					log.debug("id: " + c.getConceptId());
				}
			}
		}
		
		return parents;
		
	}
	
	public void updateConceptSetDerived() throws DAOException {
		// remove all of the rows in the derived table
		sessionFactory.getCurrentSession().createQuery("delete from ConceptSetDerived").executeUpdate();
		
		try {
			// remake the derived table by copying over the basic concept_set table
			sessionFactory.getCurrentSession().connection().prepareStatement("insert into concept_set_derived (concept_id, concept_set, sort_weight) select cs.concept_id, cs.concept_set, cs.sort_weight from concept_set cs where not exists (select concept_id from concept_set_derived csd where csd.concept_id = cs.concept_id and csd.concept_set = cs.concept_set)").execute();
		
			// burst the concept sets -- make grandchildren direct children of grandparents
			sessionFactory.getCurrentSession().connection().prepareStatement("insert into concept_set_derived (concept_id, concept_set, sort_weight) select cs1.concept_id, cs2.concept_set, cs1.sort_weight from concept_set cs1 join concept_set cs2 where cs2.concept_id = cs1.concept_set and not exists (select concept_id from concept_set_derived csd where csd.concept_id = cs1.concept_id and csd.concept_set = cs2.concept_set)").execute();
			
			// burst the concept sets -- make greatgrandchildren direct child of greatgrandparents
			sessionFactory.getCurrentSession().connection().prepareStatement("insert into concept_set_derived (concept_id, concept_set, sort_weight) select cs1.concept_id, cs3.concept_set, cs1.sort_weight from concept_set cs1 join concept_set cs2 join concept_set cs3 where cs1.concept_set = cs2.concept_id and cs2.concept_set = cs3.concept_id and not exists (select concept_id from concept_set_derived csd where csd.concept_id = cs1.concept_id and csd.concept_set = cs3.concept_set)").execute();
			
			// TODO This 'algorithm' only solves three layers of children.  Options for correction:
			//	1) Add a few more join statements to cover 5 layers (conceivable upper limit of layers)
			//	2) Find the deepest layer and programmatically create the sql statements
			//	3) Run the joins on 
		}
		catch (SQLException e) {
			throw new DAOException (e);
		}
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#addConceptProposal(org.openmrs.ConceptProposal)
	 */
	public void createConceptProposal(ConceptProposal cp) throws DAOException {
		sessionFactory.getCurrentSession().save(cp);
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#updateConceptProposal(org.openmrs.ConceptProposal)
	 */
	public void updateConceptProposal(ConceptProposal cp) throws DAOException {
		if (cp.getConceptProposalId() == null)
			createConceptProposal(cp);
		else {
			sessionFactory.getCurrentSession().update(cp);
		}
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#mrnGeneratorLog(java.lang.String,java.lang.Integer,java.lang.Integer)
	 */
	public void mrnGeneratorLog(String site, Integer start, Integer count) {
		try {
			String sql = "insert into ";
			sql += OpenmrsConstants.DATABASE_BUSINESS_NAME + ".ext_mrn_log ";
			sql += "(date_generated, generated_by, site, mrn_first, mrn_count) values (?, ?, ?, ?, ?)";
			
			PreparedStatement ps = sessionFactory.getCurrentSession().connection().prepareStatement(sql);
			
			ps.setTimestamp(1, new Timestamp(new Date().getTime()));
			ps.setInt(2, Context.getAuthenticatedUser().getUserId());
			ps.setString(3, site);
			ps.setInt(4, start);
			ps.setInt(5, count);
			ps.execute();
		}
		catch (Exception e) {
			throw new DAOException("Error generating mrn log", e);
		}
	}
	
	/**
	 * @see org.openmrs.api.db.AdministrationService#getMRNGeneratorLog()
	 */
	public Collection getMRNGeneratorLog() {
		Collection<Map<String, Object>> log = new Vector<Map<String, Object>>();
		
		try {
			Map<String, Object> row;
			
			String sql = "select * from ";
			sql += OpenmrsConstants.DATABASE_BUSINESS_NAME + ".ext_mrn_log ";
			sql += "order by mrn_log_id desc";
			
			PreparedStatement ps = sessionFactory.getCurrentSession().connection().prepareStatement(sql);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				row = new HashMap<String, Object>();
				row.put("date", rs.getTimestamp("date_generated"));
				row.put("user", rs.getString("generated_by"));
				row.put("site", rs.getString("site"));
				row.put("first", rs.getInt("mrn_first"));
				row.put("count", rs.getInt("mrn_count"));
				log.add(row);
			}
		}
		catch (Exception e) {
			throw new DAOException("Error getting mrn log", e);
		}
		
		return log;
	}

	public void createReportObject(AbstractReportObject ro) throws DAOException {
		ro.setCreator(Context.getAuthenticatedUser());
		ro.setDateCreated(new Date());
		ro.setVoided(new Boolean(false));
		
		ReportObjectWrapper wrappedReportObject = new ReportObjectWrapper(ro);

		sessionFactory.getCurrentSession().save(wrappedReportObject);
	}

	public void updateReportObject(AbstractReportObject ro) throws DAOException {
		if (ro.getReportObjectId() == null)
			createReportObject(ro);
		else {
			sessionFactory.getCurrentSession().clear();
			ReportObjectWrapper wrappedReportObject = new ReportObjectWrapper(ro);
			sessionFactory.getCurrentSession().saveOrUpdate(wrappedReportObject);
		}
	}	
	
	public void deleteReportObject(Integer reportObjectId) throws DAOException {
		ReportObjectWrapper wrappedReportObject = new ReportObjectWrapper();
		wrappedReportObject = (ReportObjectWrapper)sessionFactory.getCurrentSession().get(ReportObjectWrapper.class, reportObjectId);
		
		sessionFactory.getCurrentSession().delete(wrappedReportObject);
	}
	
	
	public String getGlobalProperty(String propertyName) throws DAOException {
		GlobalProperty gp = (GlobalProperty)sessionFactory.getCurrentSession().get(GlobalProperty.class, propertyName);
		
		if (gp == null)
			return null;

		return gp.getPropertyValue();
	}
	
	@SuppressWarnings("unchecked")
    public List<GlobalProperty> getGlobalProperties() throws DAOException {
		log.debug("getting all global properties");

		return sessionFactory.getCurrentSession().createCriteria(GlobalProperty.class).list();
	}
	
	@SuppressWarnings("unchecked")
    public void setGlobalProperties(List<GlobalProperty> props) throws DAOException {
		log.debug("setting all global properties");
		// add all of the new properties
		for (GlobalProperty prop : props) {
			if (prop.getProperty() != null && prop.getProperty().length() > 0) {
				try {
					sessionFactory.getCurrentSession().saveOrUpdate(prop);
				}
				catch (HibernateException e) {
					sessionFactory.getCurrentSession().merge(prop);
				}
			}
		}
		
		// delete all properties not in this new list
		for (GlobalProperty gp : getGlobalProperties()) {
			if (!props.contains(gp))
				deleteGlobalProperty(gp.getProperty());
		}
	}

	public void deleteGlobalProperty(String propertyName) throws DAOException { 
		sessionFactory.getCurrentSession().createQuery("delete from GlobalProperty where property = :p")
					.setParameter("p", propertyName)
					.executeUpdate();
	}
	
	public void setGlobalProperty(String propertyName, String propertyValue) throws DAOException {
		if (propertyName != null && propertyName != "") {
			sessionFactory.getCurrentSession().createQuery("update GlobalProperty set propertyValue = :v where property = :n")
					.setParameter("v", propertyValue)
					.setParameter("n", propertyName)
					.executeUpdate();
		}
	}

	public void addGlobalProperty(String propertyName, String propertyValue) throws DAOException {
		GlobalProperty prop = new GlobalProperty(propertyName, propertyValue);
		sessionFactory.getCurrentSession().save(prop);
	}

	@SuppressWarnings("unchecked")
	public List<DataEntryStatistic> getDataEntryStatistics(Date fromDate, Date toDate, String encounterColumn, String orderColumn, String groupBy) throws DAOException {
		if (groupBy == null) groupBy = "";
		if (groupBy.length() != 0)
			groupBy = "enc." + groupBy;
		
		
		// for all encounters, find user, form name, and number of entries
		
		// default userColumn to creator
		if (encounterColumn == null)
			encounterColumn = "creator";
		encounterColumn = encounterColumn.toLowerCase();
		
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Encounter.class, "enc");
		
		ProjectionList projections = Projections.projectionList();
		if (groupBy.length() > 0)
			projections.add(Projections.groupProperty(groupBy));
		projections.add(Projections.groupProperty("enc." + encounterColumn));
		projections.add(Projections.groupProperty("enc.form"));
		projections.add(Projections.count("enc." + encounterColumn));
		
		crit.setProjection(projections);
		
		if (fromDate != null)
			crit.add(Expression.ge("enc.dateCreated", fromDate));
		if (toDate != null) {
			crit.add(Expression.le("enc.dateCreated", toDate));
		}
		
		List<Object[]> l = crit.list();
		List<DataEntryStatistic> ret = new ArrayList<DataEntryStatistic>();
		for (Object[] holder : l) {
			DataEntryStatistic s = new DataEntryStatistic();
			int offset = 0;
			if (groupBy.length() > 0) {
				s.setGroupBy(holder[0]);
				offset = 1;
			}
			
			s.setUser((User) holder[0 + offset]);
			s.setEntryType(((Form)holder[1 + offset]).getName());
			s.setNumberOfEntries((Integer) holder[2 + offset]);
			ret.add(s);
		}
		
		
		// default userColumn to creator
		if (orderColumn == null)
			orderColumn = "creator";
		orderColumn = orderColumn.toLowerCase();
		
		
		// for orders, count how many were created. (should eventually count something with voided/changed)
		String hql = "select o." + orderColumn + ", o.orderType.name, count(*) " +
				"from Order o ";
		if (fromDate != null || toDate != null) {
			String s = "where ";
			if (fromDate != null)
				s += "o.dateCreated >= :fromDate ";
			if (toDate != null) {
				if (fromDate != null)
					s += "and ";
				s += "o.dateCreated <= :toDate ";
			}
			hql += s;
		}
		hql += "group by o." + orderColumn + ", o.orderType.name ";
		Query q = sessionFactory.getCurrentSession().createQuery(hql);
		if (fromDate != null)
			q.setParameter("fromDate", fromDate);
		if (toDate != null)
			q.setParameter("toDate", toDate);
		l = q.list();
		for (Object[] holder : l) {
			DataEntryStatistic s = new DataEntryStatistic();
			s.setUser((User) holder[1]);
			s.setEntryType((String) holder[0]);
			s.setNumberOfEntries((Integer) holder[2]);
			ret.add(s);
		}
		
		return ret;
	}
}