import InputItem from '@/components/common/inputs/inputItem';
import PrimaryButton from '@/components/common/buttons/primaryButton';
import styles from '../userRegist.module.css';

export default function Login() {
  return (
    <div className={styles.container}>
      <div className={styles.section}>
        <div className={styles.title}>회원가입</div>
        <InputItem label="아이디" placeholder="6자 이상의 영문, 숫자" />
        <InputItem label="닉네임" placeholder="2자 이상의 영문, 한글" />
        <InputItem
          label="비밀번호"
          placeholder="8자 이상의 영문, 숫자, 특수기호"
        />
        <InputItem label="비밀번호 확인" placeholder="비밀번호 확인" />
        <InputItem label="생년월일" isBirth />
        <PrimaryButton label="가입하기" size="medium" />
      </div>
    </div>
  );
}
