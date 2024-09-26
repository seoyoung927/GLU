/* eslint-disable jsx-a11y/img-redundant-alt */
/* eslint-disable @next/next/no-img-element */
import { MypageUser } from '@/types/UserTypes';
import styles from './mytestGrade.module.css';
import BarGraph from '../common/graphs/barGraph';

interface MytestGradeProps {
  userInfo: MypageUser;
}

export default function MytestGrade({ userInfo }: MytestGradeProps) {
  return (
    <div className={styles.container}>
      <div className={styles.section}>
        <div className={styles['section-title']}>내 캐릭터</div>
        <div className={styles['img-container']}>
          <img
            src={userInfo.imageUrl}
            alt="character image"
            className={styles['character-img']}
          />
        </div>
        <div className={styles['flex-line']}>
          <div className={styles['exp-title']}>{userInfo.stage}단계</div>
          <div className={styles['exp-container']}>
            <BarGraph maxScore={100} currentScore={userInfo.exp} />
          </div>
        </div>
      </div>
      <div className={styles.section}>
        <div className={styles['section-title']}>영역별 점수</div>
        <div>삼각 그래프</div>
        <div>성적별 그래프</div>
      </div>
    </div>
  );
}
