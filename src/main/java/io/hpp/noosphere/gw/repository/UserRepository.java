package io.hpp.noosphere.gw.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.gw.domain.QUser;
import io.hpp.noosphere.gw.domain.User;
import io.hpp.noosphere.gw.repository.util.QuerydslUtil;
import io.hpp.noosphere.common.service.util.CommonUtils;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {
    String USERS_BY_API_KEY_CACHE = "usersByApiKey";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";
}

interface UserRepositoryCustom {
    @Cacheable(cacheNames = UserRepository.USERS_BY_EMAIL_CACHE, unless = "#result == null")
    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByEmail(String email, Boolean activated);

    @Cacheable(cacheNames = UserRepository.USERS_BY_API_KEY_CACHE, unless = "#result == null")
    Optional<User> findOneByApiKey(String apiKey);

    Optional<User> findOneByApiKey(String apiKey, Boolean activated);
    Optional<User> findOneByWalletAddressAndEmail(String walletAddress, String email, Boolean activated);
    Optional<User> findOneByWalletAddress(String walletAddress, Boolean activated);

    Optional<User> findOneByEmailAndWalletAddressOrApiKey(String email, String walletAddress, String apiKey, Boolean activated);

    User findOneActiveById(String id);

    User findOneById(String id);

    Page<User> search(String name, Boolean activated, Pageable pageable);
}

@Repository
class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    public UserRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<User> findOneByEmail(String email) {
        return findOneByEmail(email, Boolean.TRUE);
    }

    @Override
    public Optional<User> findOneByEmail(String email, Boolean activated) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.email.eq(email));
        if (activated != null) {
            builder.and(qUser.activated.eq(activated));
        }
        return Optional.ofNullable(jpaQueryFactory.selectFrom(qUser).where(builder).fetchOne());
    }

    @Override
    public Optional<User> findOneByApiKey(String apiKey) {
        return findOneByApiKey(apiKey, Boolean.TRUE);
    }

    @Override
    public Optional<User> findOneByApiKey(String apiKey, Boolean activated) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.apiKey.eq(apiKey));
        if (activated != null) {
            builder.and(qUser.activated.eq(activated));
        }
        return Optional.ofNullable(jpaQueryFactory.selectFrom(qUser).where(builder).fetchOne());
    }

    @Override
    public Optional<User> findOneByWalletAddressAndEmail(String walletAddress, String email, Boolean activated) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.email.eq(email));
        builder.and(qUser.walletAddress.eq(walletAddress));
        if (activated != null) {
            builder.and(qUser.activated.eq(activated));
        }
        return Optional.ofNullable(jpaQueryFactory.selectFrom(qUser).where(builder).fetchOne());
    }

    @Override
    public Optional<User> findOneByWalletAddress(String walletAddress, Boolean activated) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.walletAddress.eq(walletAddress));
        if (activated != null) {
            builder.and(qUser.activated.eq(activated));
        }
        return Optional.ofNullable(jpaQueryFactory.selectFrom(qUser).where(builder).fetchOne());
    }

    @Override
    public Optional<User> findOneByEmailAndWalletAddressOrApiKey(String email, String walletAddress, String apiKey, Boolean activated){
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.email.eq(email));

        BooleanBuilder orPredicate = new BooleanBuilder();
        orPredicate.or(qUser.walletAddress.eq(walletAddress));
        orPredicate.or(qUser.apiKey.eq(apiKey));
        builder.and(orPredicate);

        if (activated != null) {
            builder.and(qUser.activated.eq(activated));
        }
        return Optional.ofNullable(jpaQueryFactory.selectFrom(qUser).where(builder).fetchOne());
    }


    @Override
    public User findOneActiveById(String id) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.id.eq(id));
        builder.and(qUser.activated.eq(Boolean.TRUE));
        return jpaQueryFactory.selectFrom(qUser).where(builder).fetchOne();
    }

    @Override
    public User findOneById(String id) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.id.eq(id));
        return jpaQueryFactory.selectFrom(qUser).where(builder).fetchOne();
    }

    @Override
    public Page<User> search(String name, Boolean activated, Pageable pageable) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        if (activated != null) {
            builder.and(qUser.activated.eq(activated));
        }
        if (CommonUtils.isValid(name)) {
            builder.and(qUser.name.containsIgnoreCase(name));
        }
        if (builder.hasValue()) {
            JPQLQuery<User> query = jpaQueryFactory.selectFrom(qUser).where(builder);
            return QuerydslUtil.fetchPage(query, pageable);
        } else {
            return Page.empty();
        }
    }
}
